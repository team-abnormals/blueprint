package com.teamabnormals.blueprint.core.data.server;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlice;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class BlueprintDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {
	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.BIOME, BlueprintDatapackBuiltinEntriesProvider::bootstrapBiomes).add(BlueprintDataPackRegistries.MODDED_BIOME_SLICES, BlueprintDatapackBuiltinEntriesProvider::bootstrapSlices);

	public BlueprintDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, BUILDER, Set.of(Blueprint.MOD_ID));
	}

	public static void bootstrapBiomes(BootstapContext<Biome> context) {
		HolderGetter<PlacedFeature> placedFeatureHolderGetter = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarverHolderGetter = context.lookup(Registries.CONFIGURED_CARVER);
		context.register(BlueprintBiomes.ORIGINAL_SOURCE_MARKER, OverworldBiomes.theVoid(placedFeatureHolderGetter, configuredWorldCarverHolderGetter));
	}

	private static void bootstrapSlices(BootstapContext<ModdedBiomeSlice> context) {
		var originalsKey = ResourceKey.create(BlueprintDataPackRegistries.MODDED_BIOME_SLICES, new ResourceLocation(Blueprint.MOD_ID, "originals"));
		context.register(originalsKey, new ModdedBiomeSlice(100, BiomeUtil.OriginalModdedBiomeProvider.INSTANCE, LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END));
	}
}
