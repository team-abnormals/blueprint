package com.teamabnormals.blueprint.common.remolder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.remolder.data.MoldingTypes;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.EmptyResourceSelector;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used for internal storage, reloading, and applying of remolders from packs.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RemolderLoader {
	private static final Gson GSON = new Gson();
	private final HashMap<String, IdentityHashMap<MoldingTypes.MoldingType<?>, Pair<Map<String, TreeSet<Entry>>, ArrayList<Pair<Predicate<ResourceLocation>, Entry>>>>> fileExtensionToEntries = new HashMap<>();
	private final RemoldingCompiler compiler;
	private final PackType packType;

	public RemolderLoader(CloseableResourceManager manager, PackType packType) {
		this.packType = packType;
		RemoldingCompiler.ExportEntry[] exports;
		try (Reader reader = manager.getResourceOrThrow(Blueprint.location("remolder.json")).openAsReader()) {
			JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
			var dataResult = Settings.CODEC.decode(JsonOps.INSTANCE, element);
			var dataResultError = dataResult.error();
			if (dataResultError.isPresent()) throw new JsonParseException(dataResultError.get().message());
			exports = dataResult.result().get().getFirst().exports;
		} catch (IOException | JsonParseException exception) {
			Blueprint.LOGGER.error("Error loading " + packType.getDirectory() + " Remolder settings, using default settings instead!", exception);
			exports = new RemoldingCompiler.ExportEntry[0];
		}
		this.compiler = new RemoldingCompiler(this.getClass().getClassLoader(), exports);
	}

	public void reloadRemolders(CloseableResourceManager manager, Executor executor) {
		var fileExtensionToEntries = this.fileExtensionToEntries;
		fileExtensionToEntries.clear();
		FileToIdConverter fileToIdConverter = FileToIdConverter.json("remolders");
		var resources = fileToIdConverter.listMatchingResources(manager);
		ArrayList<CompletableFuture<Void>> futures = new ArrayList<>(resources.size());
		String packTypeDirectory = this.packType.getDirectory() + "/";
		AtomicInteger successfulCount = new AtomicInteger();
		for (var entry : resources.entrySet()) {
			futures.add(CompletableFuture.runAsync(() -> {
				ResourceLocation entryKey = entry.getKey();
				ResourceLocation entryId = fileToIdConverter.fileToId(entryKey);
				entryKey = ResourceLocation.fromNamespaceAndPath(entryKey.getNamespace(), packTypeDirectory + entryKey.getPath());
				try (Reader reader = entry.getValue().openAsReader()) {
					JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
					var remolderEntryDataResult = RemolderEntry.CODEC.decode(JsonOps.INSTANCE, element);
					var remolderEntryError = remolderEntryDataResult.error();
					if (remolderEntryError.isPresent())
						throw new JsonParseException(remolderEntryError.get().message());
					RemolderEntry remolderEntry = remolderEntryDataResult.result().get().getFirst();
					if (remolderEntry == RemolderEntry.NOOP) return;
					ResourceSelector<?> pathSelector = remolderEntry.pathSelector().getResourceSelector();
					if (pathSelector == EmptyResourceSelector.INSTANCE) return;
					MoldingTypes.MoldingType<?> moldingType = remolderEntry.molding();
					Remolding<?> remolding;
					try {
						remolding = this.compiler.compile(entryKey.toString(), moldingType.dataType(), moldingType.factory(), remolderEntry.remolder());
					} catch (Throwable throwable) {
						throw new JsonParseException("Error while generating modifications for Remolder '" + entryKey + "': " + throwable);
					}
					synchronized (this) {
						for (String fileExtension : moldingType.fileExtensions()) {
							var entries = fileExtensionToEntries.computeIfAbsent(fileExtension, __ -> {
								return new IdentityHashMap<>();
							}).computeIfAbsent(moldingType, __ -> {
								return Pair.of(new HashMap<>(), new ArrayList<>());
							});
							var either = pathSelector.select();
							var locations = either.left();
							var packs = remolderEntry.packs();
							if (locations.isPresent()) {
								var directRemoldings = entries.getFirst();
								Entry remoldingEntry = new Entry(entryKey, packs == null ? s -> true : packs::contains, remolderEntry.priority(), remolding);
								locations.get().forEach(location -> directRemoldings.computeIfAbsent(location.toString(), __ -> new TreeSet<>()).add(remoldingEntry));
							} else
								entries.getSecond().add(Pair.of(either.right().get(), new Entry(entryKey, packs == null ? s -> true : packs::contains, remolderEntry.priority(), remolding)));
						}
					}
					successfulCount.getAndIncrement();
				} catch (IllegalArgumentException | IOException | JsonParseException exception) {
					Blueprint.LOGGER.error("Couldn't load remolder file {} from {}", entryId, entryKey, exception);
				}
			}, executor));
		}
		CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
		for (var entry : fileExtensionToEntries.entrySet()) {
			entry.getValue().values().forEach(remolders -> {
				remolders.getSecond().sort(Comparator.comparing(Pair::getSecond));
			});
		}
		Blueprint.LOGGER.info("Successfully loaded {} {} remolders!", successfulCount.get(), this.packType.getDirectory());
	}

	@SuppressWarnings("unchecked")
	@Nullable
	private Function<Resource, Resource> getResourceFunction(ResourceLocation location) {
		String locationString = location.toString();
		int lastIndexOfDot = locationString.lastIndexOf('.');
		if (lastIndexOfDot >= 0) {
			String extension = locationString.substring(lastIndexOfDot + 1);
			var entriesForExtension = this.fileExtensionToEntries.get(extension);
			if (entriesForExtension != null) {
				String locationWithoutExtension = locationString.substring(0, lastIndexOfDot);
				ResourceLocation resourceLocationWithoutExtension = ResourceLocation.parse(locationWithoutExtension);
				boolean foundNone = true;
				Pair<MoldingTypes.MoldingType<?>, Collection<Entry>>[] typeEntries = new Pair[entriesForExtension.size()];
				int i = 0;
				for (var entry : entriesForExtension.entrySet()) {
					var value = entry.getValue();
					var indirectRemolders = value.getSecond();
					Collection<Entry> entriesForLocation = value.getFirst().get(locationWithoutExtension);
					if (!indirectRemolders.isEmpty()) {
						TreeSet<Entry> mergedRemolders = new TreeSet<>();
						for (var filterAndRemolding : indirectRemolders) {
							if (!filterAndRemolding.getFirst().test(resourceLocationWithoutExtension)) continue;
							mergedRemolders.add(filterAndRemolding.getSecond());
						}
						if (!mergedRemolders.isEmpty()) {
							if (entriesForLocation != null) mergedRemolders.addAll(entriesForLocation);
							entriesForLocation = mergedRemolders;
						} else if (entriesForLocation == null) continue;
					} else if (entriesForLocation == null) continue;
					foundNone = false;
					typeEntries[i++] = Pair.of(entry.getKey(), entriesForLocation);
				}
				if (foundNone) return null;
				int finalI = i;
				return resource -> {
					for (int j = 0; j < finalI; j++) {
						var moldingTypeWithEntries = typeEntries[j];
						resource = moldingTypeWithEntries.getFirst().remold(locationWithoutExtension, resource, moldingTypeWithEntries.getSecond());
					}
					return resource;
				};
			}
		}
		return null;
	}

	public Optional<Resource> getResource(ResourceLocation location, Optional<Resource> resource) {
		var function = this.getResourceFunction(location);
		if (function == null) return resource;
		return resource.map(function);
	}

	public List<Resource> getResourceStack(List<Resource> stack, ResourceLocation location) {
		Function<Resource, Resource> resourceFunction = this.getResourceFunction(location);
		if (resourceFunction == null) return stack;
		return new DataUtil.ReadMappedList<>(stack, resourceFunction);
	}

	public Map<ResourceLocation, Resource> listResources(Map<ResourceLocation, Resource> map) {
		for (var entry : map.entrySet()) {
			Function<Resource, Resource> resourceFunction = this.getResourceFunction(entry.getKey());
			if (resourceFunction == null) continue;
			entry.setValue(resourceFunction.apply(entry.getValue()));
		}
		return map;
	}

	public Map<ResourceLocation, List<Resource>> listResourceStacks(Map<ResourceLocation, List<Resource>> map) {
		for (var entry : map.entrySet()) {
			Function<Resource, Resource> resourceFunction = this.getResourceFunction(entry.getKey());
			if (resourceFunction == null) continue;
			entry.setValue(new DataUtil.ReadMappedList<>(entry.getValue(), resourceFunction));
		}
		return map;
	}

	public record Settings(RemoldingCompiler.ExportEntry[] exports) {
		private static final Codec<RemoldingCompiler.ExportEntry[]> EXPORTS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(map -> {
			var entries = map.entrySet();
			if (entries.isEmpty()) return new RemoldingCompiler.ExportEntry[0];
			RemoldingCompiler.ExportEntry[] exports = new RemoldingCompiler.ExportEntry[entries.size()];
			int exportsCount = 0;
			for (var entry : entries) {
				Pattern pattern = Pattern.compile(String.valueOf(entry.getValue()));
				exports[exportsCount++] = new RemoldingCompiler.ExportEntry(entry.getKey(), pattern.pattern(), string -> pattern.matcher(string).matches());
			}
			return exports;
		}, array -> Stream.of(array).collect(Collectors.toMap(RemoldingCompiler.ExportEntry::folder, RemoldingCompiler.ExportEntry::pattern)));
		public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					EXPORTS_CODEC.fieldOf("exports").forGetter(Settings::exports)
			).apply(instance, Settings::new);
		});
	}

	/**
	 * Record class for storing a loaded remolder's pack filter and remolding.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record Entry(ResourceLocation name, Predicate<String> packFilter, int priority, Remolding<?> remolding) implements Comparable<Entry> {
		public Entry(Predicate<String> packFilter, Remolding<?> remolding) {
			this(tryToExtractName(remolding), packFilter, 1000, remolding);
		}

		private static ResourceLocation tryToExtractName(Remolding<?> remolding) {
			String id = remolding.toString();
			int indexOfSpace = id.indexOf(' ');
			return indexOfSpace != -1 ? ResourceLocation.parse(id.substring(0, indexOfSpace)) : ResourceLocation.withDefaultNamespace("unknown");
		}

		@Override
		public int compareTo(RemolderLoader.Entry other) {
			int i = Integer.compare(this.priority, other.priority);
			return i == 0 ? this.name.compareTo(other.name) : i;
		}
	}
}
