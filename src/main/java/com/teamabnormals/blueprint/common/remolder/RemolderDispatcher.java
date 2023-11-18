package com.teamabnormals.blueprint.common.remolder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.common.remolder.data.MoldingTypes;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.EmptyResourceSelector;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public final class RemolderDispatcher {
	private static final Gson GSON = new Gson();
	private static final RemoldingCompiler REMOLDING_COMPILER = new RemoldingCompiler(ClassLoader.getSystemClassLoader());
	public static final RemolderDispatcher CLIENT_ASSETS = new RemolderDispatcher();

	private final HashMap<String, IdentityHashMap<MoldingTypes.MoldingType<?>, Pair<Map<String, List<Remolding<?>>>, ArrayList<Pair<Predicate<ResourceLocation>, Remolding<?>>>>>> fileExtensionToEntries = new HashMap<>();

	public Resource apply(ResourceLocation location, Resource resource) {
		String path = location.getPath();
		int lastIndexOfDot = path.lastIndexOf('.');
		if (lastIndexOfDot == -1) return resource;
		String extension = path.substring(lastIndexOfDot + 1);
		var entriesForExtension = this.fileExtensionToEntries.get(extension);
		if (entriesForExtension != null) {
			String locationWithoutExtension = location.toString().substring(0, lastIndexOfDot);
			for (var entry : entriesForExtension.entrySet()) {
				resource = entry.getKey().apply(locationWithoutExtension, resource, entry.getValue());
			}
		}
		return resource;
	}

	public CompletableFuture<Unit> reload(ResourceManager resourceManager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			var fileExtensionToEntries = this.fileExtensionToEntries;
			fileExtensionToEntries.clear();
			FileToIdConverter fileToIdConverter = FileToIdConverter.json("remolders");
			for (var entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
				ResourceLocation entryKey = entry.getKey();
				ResourceLocation entryId = fileToIdConverter.fileToId(entryKey);
				try (Reader reader = entry.getValue().openAsReader()) {
					JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
					var remolderEntryDataResult = RemolderEntry.CODEC.decode(JsonOps.INSTANCE, element);
					var remolderEntryError = remolderEntryDataResult.error();
					if (remolderEntryError.isPresent())
						throw new JsonParseException(remolderEntryError.get().message());
					RemolderEntry remolderEntry = remolderEntryDataResult.result().get().getFirst();
					if (remolderEntry == RemolderEntry.NOOP) continue;
					ResourceSelector<?> resourceSelector = remolderEntry.selector().getResourceSelector();
					if (resourceSelector == EmptyResourceSelector.INSTANCE) continue;
					MoldingTypes.MoldingType<?> moldingType = remolderEntry.molding();
					Remolding<?> remolding;
					try {
						remolding = REMOLDING_COMPILER.compile(entryKey.toString(), moldingType.molding(), remolderEntry.remolder().remold());
					} catch (Exception exception) {
						throw new JsonParseException("Error while generating modifications for Remolder '" + entryKey + "': " + exception);
					}
					for (String fileExtension : moldingType.fileExtensions()) {
						var entries = fileExtensionToEntries.computeIfAbsent(fileExtension, __ -> {
							return new IdentityHashMap<>();
						}).computeIfAbsent(moldingType, __ -> {
							return Pair.of(new HashMap<>(), new ArrayList<>());
						});
						var either = resourceSelector.select();
						var locations = either.left();
						if (locations.isPresent()) {
							var directRemoldings = entries.getFirst();
							locations.get().forEach(location -> directRemoldings.computeIfAbsent(location.toString(), __ -> new ArrayList<>()).add(remolding));
						} else entries.getSecond().add(Pair.of(either.right().get(), remolding));
					}
				} catch (IllegalArgumentException | IOException | JsonParseException exception) {
					Blueprint.LOGGER.error("Couldn't load remolder file {} from {}", entryId, entryKey, exception);
				}
			}
			return Unit.INSTANCE;
		}, executor);
	}
}
