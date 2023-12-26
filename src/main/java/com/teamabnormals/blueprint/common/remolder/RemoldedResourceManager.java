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
import net.minecraft.server.packs.PackResources;
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
 * Class used for wrapping {@link CloseableResourceManager} instances to alter their resources using Remolders.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RemoldedResourceManager implements CloseableResourceManager {
	private static final Gson GSON = new Gson();
	@Nullable
	private static RemoldedResourceManager CLIENT;
	@Nullable
	private static RemoldedResourceManager SERVER;
	private final HashMap<String, IdentityHashMap<MoldingTypes.MoldingType<?>, Pair<Map<String, List<Entry>>, ArrayList<Pair<Predicate<ResourceLocation>, Entry>>>>> fileExtensionToEntries = new HashMap<>();
	private final CloseableResourceManager manager;
	private final RemoldingCompiler compiler;
	private final PackType packType;
	private final boolean needsAutoReload;

	private RemoldedResourceManager(CloseableResourceManager manager, PackType packType, boolean needsAutoReload) {
		this.manager = manager;
		this.packType = packType;
		RemoldingCompiler.ExportEntry[] exports;
		try (Reader reader = manager.getResourceOrThrow(new ResourceLocation(Blueprint.MOD_ID, "remolder.json")).openAsReader()) {
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
		this.needsAutoReload = needsAutoReload;
	}

	public static RemoldedResourceManager wrapForClient(CloseableResourceManager manager) {
		return CLIENT = new RemoldedResourceManager(manager, PackType.CLIENT_RESOURCES, true);
	}

	public static RemoldedResourceManager wrapForServer(CloseableResourceManager manager, boolean shouldAutoReload) {
		return SERVER = new RemoldedResourceManager(manager, PackType.SERVER_DATA, shouldAutoReload);
	}

	@Nullable
	public static RemoldedResourceManager client() {
		return CLIENT;
	}

	@Nullable
	public static RemoldedResourceManager server() {
		return SERVER;
	}

	public boolean needsAutoReload() {
		return this.needsAutoReload;
	}

	public void reloadRemolders(Executor executor) {
		var fileExtensionToEntries = this.fileExtensionToEntries;
		fileExtensionToEntries.clear();
		FileToIdConverter fileToIdConverter = FileToIdConverter.json("remolders");
		var resources = fileToIdConverter.listMatchingResources(this.manager);
		ArrayList<CompletableFuture<Void>> futures = new ArrayList<>(resources.size());
		String packTypeDirectory = this.packType.getDirectory() + "/";
		AtomicInteger successfulCount = new AtomicInteger();
		for (var entry : resources.entrySet()) {
			futures.add(CompletableFuture.runAsync(() -> {
				ResourceLocation entryKey = entry.getKey();
				ResourceLocation entryId = fileToIdConverter.fileToId(entryKey);
				entryKey = new ResourceLocation(entryKey.getNamespace(), packTypeDirectory + entryKey.getPath());
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
						remolding = this.compiler.compile(entryKey.toString(), moldingType.molding(), remolderEntry.remolder().remold());
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
							if (locations.isPresent()) {
								var directRemoldings = entries.getFirst();
								locations.get().forEach(location -> directRemoldings.computeIfAbsent(location.toString(), __ -> new ArrayList<>()).add(new Entry(ResourceSelector.predicate(remolderEntry.packSelector()), remolding)));
							} else
								entries.getSecond().add(Pair.of(either.right().get(), new Entry(ResourceSelector.predicate(remolderEntry.packSelector()), remolding)));
						}
					}
					successfulCount.getAndIncrement();
				} catch (IllegalArgumentException | IOException | JsonParseException exception) {
					Blueprint.LOGGER.error("Couldn't load remolder file {} from {}", entryId, entryKey, exception);
				}
			}, executor));
		}
		CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
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
				Pair<MoldingTypes.MoldingType<?>, List<Entry>>[] typeEntries = new Pair[entriesForExtension.size()];
				int i = 0;
				boolean foundNone = true;
				for (var entry : entriesForExtension.entrySet()) {
					var value = entry.getValue();
					var entriesForLocation = value.getFirst().get(locationWithoutExtension);
					ResourceLocation resourceLocationWithoutExtension = new ResourceLocation(locationWithoutExtension);
					for (var filterAndRemolding : value.getSecond()) {
						if (!filterAndRemolding.getFirst().test(resourceLocationWithoutExtension)) continue;
						if (entriesForLocation == null) entriesForLocation = new ArrayList<>();
						entriesForLocation.add(filterAndRemolding.getSecond());
					}
					if (entriesForLocation == null || entriesForLocation.isEmpty()) continue;
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

	@Override
	public Set<String> getNamespaces() {
		return this.manager.getNamespaces();
	}

	@Override
	public List<Resource> getResourceStack(ResourceLocation location) {
		Function<Resource, Resource> resourceFunction = this.getResourceFunction(location);
		if (resourceFunction == null) return this.manager.getResourceStack(location);
		return new DataUtil.ReadMappedList<>(this.manager.getResourceStack(location), resourceFunction);
	}

	@Override
	public Map<ResourceLocation, Resource> listResources(String path, Predicate<ResourceLocation> locationFilter) {
		var map = this.manager.listResources(path, locationFilter);
		for (var entry : map.entrySet()) {
			Function<Resource, Resource> resourceFunction = this.getResourceFunction(entry.getKey());
			if (resourceFunction == null) continue;
			entry.setValue(resourceFunction.apply(entry.getValue()));
		}
		return map;
	}

	@Override
	public Map<ResourceLocation, List<Resource>> listResourceStacks(String path, Predicate<ResourceLocation> locationFilter) {
		var map = this.manager.listResourceStacks(path, locationFilter);
		for (var entry : map.entrySet()) {
			Function<Resource, Resource> resourceFunction = this.getResourceFunction(entry.getKey());
			if (resourceFunction == null) continue;
			entry.setValue(new DataUtil.ReadMappedList<>(entry.getValue(), resourceFunction));
		}
		return map;
	}

	@Override
	public Stream<PackResources> listPacks() {
		return this.manager.listPacks();
	}

	@Override
	public Optional<Resource> getResource(ResourceLocation location) {
		var function = this.getResourceFunction(location);
		if (function == null) return this.manager.getResource(location);
		return this.manager.getResource(location).map(function);
	}

	@Override
	public void close() {
		this.manager.close();
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
	 * Record class for storing a loaded remolder's pack selector and remolding.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record Entry(Predicate<ResourceLocation> packSelector, Remolding<?> remolding) {}
}
