package com.minecraftabnormals.abnormals_core.common.world.gen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;

import javax.annotation.Nullable;

/**
 * The functional interface for getting the edge biome for a biome.
 *
 * @see com.minecraftabnormals.abnormals_core.core.util.BiomeUtil#getEdgeBiome(ResourceKey, Context, ResourceKey, ResourceKey, ResourceKey, ResourceKey)
 */
@FunctionalInterface
public interface EdgeBiomeProvider {
	/**
	 * Get the bordering {@link Biome} given the surrounding {@link Biome}s.
	 *
	 * @param context    The {@link Context} for any randomness.
	 * @param northBiome The {@link Biome} {@link ResourceKey} of the biome to the north.
	 * @param westBiome  The {@link Biome} {@link ResourceKey} of the biome to the west.
	 * @param southBiome The {@link Biome} {@link ResourceKey} of the biome to the south.
	 * @param eastBiome  The {@link Biome} {@link ResourceKey} of the biome to the east.
	 * @return The {@link Biome} {@link ResourceKey} to generate, or null to defer to vanilla logic.
	 */
	@Nullable
	ResourceKey<Biome> getEdgeBiome(Context context, ResourceKey<Biome> northBiome, ResourceKey<Biome> westBiome, ResourceKey<Biome> southBiome, ResourceKey<Biome> eastBiome);
}
