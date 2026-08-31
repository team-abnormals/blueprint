package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import com.teamabnormals.blueprint.core.events.SimpleEvent;
import com.teamabnormals.blueprint.core.other.BlueprintDataMaps;
import com.teamabnormals.blueprint.core.other.BlueprintDataMaps.ModdedBiomeSliceSizeEntry;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import com.teamabnormals.blueprint.core.util.BiomeUtil.ModdedBiomeProvider;
import com.teamabnormals.blueprint.core.util.BiomeUtil.OriginalModdedBiomeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.*;

/**
 * The manager class for Blueprint's modded biome sources system.
 * <p>This class handles the applying of {@link ModdedBiomeSlice} instances registered by datapacks or events.</p>
 * <p>
 *     Use {@link #PROVIDER_LOADING_EVENT} to modify the biome placement of <b>existing</b> slices.
 *     Remolders also work as an alternative but may be more tedious to use.
 * </p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ModdedBiomeSlicesManager {
	public static final SimpleEvent<ProviderLoadingEvent> PROVIDER_LOADING_EVENT = new SimpleEvent<>(ProviderLoadingEvent.class, listeners -> {
		Arrays.sort(listeners, Comparator.comparingInt(ProviderLoadingEvent::priority));
		return (server, key, levels, provider) -> {
			for (var listener : listeners)
				provider = listener.provide(server, key, levels, provider);
			return provider;
		};
	});

	@SuppressWarnings("deprecation")
	public static void onServerAboutToStart(MinecraftServer server) {
		RegistryAccess registryAccess = server.registryAccess();
		var slices = registryAccess.registryOrThrow(BlueprintDataPackRegistries.MODDED_BIOME_SLICES).entrySet();
		if (slices.isEmpty()) return;

		Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
		Holder<Biome> originalSourceMarker = biomeRegistry.getHolderOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER);
		HashMap<ResourceLocation, ArrayList<Pair<ResourceLocation, ModdedBiomeSlice>>> assignedSlices = new HashMap<>();
		long seed = server.getWorldData().worldGenOptions().seed();
		for (var unassignedSlice : slices) {
			ModdedBiomeSlice slice = unassignedSlice.getValue();
			if (slice.weight() <= 0) continue;
			var key = unassignedSlice.getKey();
			var levels = slice.levels();
			ModdedBiomeProvider provider = slice.provider();
			ModdedBiomeProvider newProvider = PROVIDER_LOADING_EVENT.getInvoker().provide(server, key, levels, provider);
			if (newProvider != provider) {
				provider = newProvider;
				slice = new ModdedBiomeSlice(levels, slice.weight(), provider);
			}
			if (provider != OriginalModdedBiomeProvider.INSTANCE) {
				var additionalPossibleBiomes = provider.getAdditionalPossibleBiomes(biomeRegistry);
				if (additionalPossibleBiomes.isEmpty() || (additionalPossibleBiomes.size() == 1 && additionalPossibleBiomes.contains(originalSourceMarker))) continue;
				provider.finalize(server, seed);
			}
			for (var level : levels)
				assignedSlices.computeIfAbsent(level.location(), __ -> new ArrayList<>()).add(Pair.of(key.location(), slice));
		}
		assignedSlices.forEach((location, pairs) -> {
			pairs.sort(Comparator.comparing(Pair::getFirst, ResourceLocation::compareNamespaced));
		});

		int defaultSize = BlueprintConfig.COMMON.defaultModdedBiomeSliceSize.get();
		if (defaultSize <= 0) {
			Blueprint.LOGGER.warn("Found a non-positive value for the default slice size! Slice size 9 will be used instead.");
			defaultSize = 9;
		}

		Registry<LevelStem> dimensions = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
			ResourceLocation location = entry.getKey().location();
			var slicesForLevel = assignedSlices.get(location);
			if (slicesForLevel == null) continue;
			int sliceCount = slicesForLevel.size();
			// Don't add slices to this level if it has no meaningful slices
			if (sliceCount == 0 || (sliceCount == 1 && slicesForLevel.getFirst().getSecond().provider() == OriginalModdedBiomeProvider.INSTANCE))
				continue;
			ChunkGenerator chunkGenerator = entry.getValue().generator();
			BiomeSource source = chunkGenerator.getBiomeSource();
			// Checking specifically for an instance of MultiNoiseBiomeSource isn't reliable because mods may alter the biome source before we do
			// If we do replace something we shouldn't then players can remove providers in a datapack
			if (source instanceof FixedBiomeSource || source instanceof CheckerboardColumnBiomeSource) continue;
			int size = defaultSize;
			ModdedBiomeSliceSizeEntry sizeEntry = dimensions.getData(BlueprintDataMaps.MODDED_BIOME_SLICE_SIZES, entry.getKey());
			if (sizeEntry != null && sizeEntry.size() > 0)
				size = sizeEntry.size();
			ModdedBiomeSource moddedBiomeSource = new ModdedBiomeSource(biomeRegistry, source, slicesForLevel, size, seed, location.hashCode());
			chunkGenerator.biomeSource = moddedBiomeSource;
			chunkGenerator.featuresPerStep = Lazy.of(() -> {
				return FeatureSorter.buildFeaturesPerStep(List.copyOf(moddedBiomeSource.possibleBiomes()), (biomeHolder) -> {
					return chunkGenerator.getBiomeGenerationSettings(biomeHolder).features();
				}, true);
			});
		}
	}

	/**
	 * Functional interface used for changing a loading biome provider.
	 * <p>Useful for global or systematic modifications to existing slices.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	@FunctionalInterface
	public interface ProviderLoadingEvent {
		/**
		 * Provides the event with the new or existing provider to use.
		 * <p>Return {@code provider} for no change.</p>
		 *
		 * @param server   The server of the world.
		 * @param key      The key of the slice that owns the provider.
		 * @param levels   The levels/dimensions that the slice generates in.
		 * @param provider The existing {@link ModdedBiomeProvider} instance.
		 * @return The new or existing {@link ModdedBiomeProvider} to use.
		 */
		ModdedBiomeProvider provide(MinecraftServer server, ResourceKey<ModdedBiomeSlice> key, HashSet<ResourceKey<LevelStem>> levels, ModdedBiomeProvider provider);

		/**
		 * Gets the priority of this listener.
		 *
		 * @return The priority of this listener.
		 */
		default int priority() {
			return 1000;
		}
	}
}
