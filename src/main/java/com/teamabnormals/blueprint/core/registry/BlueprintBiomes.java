package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BiomeSubRegistryHelper;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BlueprintBiomes {
	private static final BiomeSubRegistryHelper HELPER = Blueprint.REGISTRY_HELPER.getBiomeSubHelper();

	public static final BiomeSubRegistryHelper.KeyedBiome ORIGINAL_SOURCE_MARKER = HELPER.createBiome("original_source_marker", OverworldBiomes::theVoid);
}
