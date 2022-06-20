package com.teamabnormals.blueprint.core.endimator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Handles the data-driven internals for {@link Endimation} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
@SuppressWarnings("deprecation")
public final class EndimationLoader implements PreparableReloadListener {
	private static final JsonParser PARSER = new JsonParser();
	private final BiMap<ResourceLocation, Endimation> registry = HashBiMap.create();

	/**
	 * Gets the {@link Endimation} mapped to a given {@link ResourceLocation} key.
	 *
	 * @param key A {@link ResourceLocation} key to use to look up its {@link Endimation}.
	 * @return The {@link Endimation} mapped to a given {@link ResourceLocation} key, or null if no such mapping exists.
	 */
	@Nullable
	public Endimation getEndimation(ResourceLocation key) {
		return this.registry.get(key);
	}

	/**
	 * Gets {@link ResourceLocation} key for a given {@link Endimation}.
	 *
	 * @param endimation An {@link Endimation} to use to look up its {@link ResourceLocation} key.
	 * @return The {@link ResourceLocation} key for a given {@link Endimation}, or null if no such key exists.
	 */
	@Nullable
	public ResourceLocation getKey(Endimation endimation) {
		return this.registry.inverse().get(endimation);
	}

	@Override
	public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, Endimation> endimations = new HashMap<>();
			for (var entry : manager.listResources("endimations", (location) -> location.getPath().endsWith(".json")).entrySet()) {
				try (Reader reader = entry.getValue().openAsReader()) {
					var dataResult = Endimation.CODEC.decode(JsonOps.INSTANCE, PARSER.parse(reader));
					var error = dataResult.error();
					if (error.isPresent()) {
						throw new JsonParseException(error.get().message());
					} else {
						ResourceLocation location = entry.getKey();
						String path = location.getPath();
						ResourceLocation adjustedLocation = new ResourceLocation(location.getNamespace(), path.substring(12, path.length() - 5));
						if (endimations.put(adjustedLocation, dataResult.result().get().getFirst()) != null) {
							Blueprint.LOGGER.warn("Loaded Duplicate Endimation: {}", adjustedLocation);
						}
					}
				} catch (Exception exception) {
					Blueprint.LOGGER.error("Error while loading Endimation: {}", entry.getKey(), exception);
				}
			}
			return endimations;
		}, executor).thenCompose(barrier::wait).thenAcceptAsync(endimations -> {
			BiMap<ResourceLocation, Endimation> registry = this.registry;
			registry.clear();
			registry.putAll(endimations);
			registry.put(PlayableEndimation.BLANK.location(), Endimation.BLANK);
			Blueprint.LOGGER.info("Endimation Loader has loaded {} endimations", registry.size());
		}, executor2);
	}
}