package com.teamabnormals.blueprint.common.world.modification;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.*;

/**
 * The manager class for Blueprint's modded biome sources system.
 * <p>This class handles the applying of {@link ModdedBiomeSlice} instances that were registered by datapacks.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ModdedBiomeSlicesManager {
	@SuppressWarnings("deprecation")
	public static void onServerAboutToStart(MinecraftServer server) {
		RegistryAccess registryAccess = server.registryAccess();
		var slices = registryAccess.registryOrThrow(BlueprintDataPackRegistries.MODDED_BIOME_SLICES).entrySet();
		if (slices.isEmpty()) return;

		HashMap<ResourceLocation, ArrayList<Pair<ResourceLocation, ModdedBiomeSlice>>> assignedSlices = new HashMap<>();
		for (var unassignedSlice : slices) {
			ModdedBiomeSlice slice = unassignedSlice.getValue();
			if (slice.weight() <= 0) return;
			slice.levels().forEach(levelStemResourceKey -> assignedSlices.computeIfAbsent(levelStemResourceKey.location(), __ -> new ArrayList<>()).add(Pair.of(unassignedSlice.getKey().location(), slice)));
		}

		CommentedConfig moddedBiomeSliceSizes = BlueprintConfig.COMMON.moddedBiomeSliceSizes.get();
		int defaultSize = moddedBiomeSliceSizes.getIntOrElse("default", 9);
		if (defaultSize <= 0) {
			Blueprint.LOGGER.warn("Found a non-positive value for the default slice size! Slice size 9 will be used instead.");
			defaultSize = 9;
		}

		Registry<LevelStem> dimensions = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
		Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
		long seed = server.getWorldData().worldGenOptions().seed();
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
			ResourceLocation location = entry.getKey().location();
			var slicesForKey = assignedSlices.get(location);
			if (slicesForKey != null && !slicesForKey.isEmpty()) {
				ChunkGenerator chunkGenerator = entry.getValue().generator();
				BiomeSource source = chunkGenerator.getBiomeSource();
				// Checking specifically for an instance of MultiNoiseBiomeSource isn't reliable because mods may alter the biome source before we do
				// If we do replace something we shouldn't then players can remove providers in a datapack
				if (!(source instanceof FixedBiomeSource) && !(source instanceof CheckerboardColumnBiomeSource)) {
					int size = moddedBiomeSliceSizes.getIntOrElse(location.toString(), defaultSize);
					if (size <= 0) size = defaultSize;
					ModdedBiomeSource moddedBiomeSource = new ModdedBiomeSource(biomeRegistry, source, slicesForKey, size, seed, location.hashCode());
					chunkGenerator.biomeSource = moddedBiomeSource;
					chunkGenerator.featuresPerStep = Suppliers.memoize(() -> {
						return FeatureSorter.buildFeaturesPerStep(List.copyOf(moddedBiomeSource.possibleBiomes()), (biomeHolder) -> {
							return chunkGenerator.getBiomeGenerationSettings(biomeHolder).features();
						}, true);
					});
				}
			}
		}
	}
}
