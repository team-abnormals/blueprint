package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public final class BlueprintBiomes {
	public static final ResourceKey<Biome> ORIGINAL_SOURCE_MARKER = ResourceKey.create(Registries.BIOME, Blueprint.location("original_source_marker"));
}
