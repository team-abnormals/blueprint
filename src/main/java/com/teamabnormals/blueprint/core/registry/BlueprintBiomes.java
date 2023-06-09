package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class BlueprintBiomes {
	public static final ResourceKey<Biome> ORIGINAL_SOURCE_MARKER = ResourceKey.create(Registries.BIOME, new ResourceLocation(Blueprint.MOD_ID, "original_source_marker"));
}
