package com.minecraftabnormals.abnormals_core.common.world.gen;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.INoiseRandom;

import javax.annotation.Nullable;

public interface EdgeBiomeProvider {
    
    /**
     * Get the bordering {@link Biome} given the surrounding {@link Biome}s.
     *
     * @param iNoiseRandom The {@link INoiseRandom} for any randomness.
     * @param northBiome The {@link Biome} {@link RegistryKey} of the biome to the north.
     * @param westBiome The {@link Biome} {@link RegistryKey} of the biome to the west.
     * @param southBiome The {@link Biome} {@link RegistryKey} of the biome to the south.
     * @param eastBiome The {@link Biome} {@link RegistryKey} of the biome to the east.
     * @return The {@link Biome} {@link RegistryKey} to generate, or null to defer to vanilla logic.
     */
    @Nullable
    RegistryKey<Biome> getEdgeBiome(INoiseRandom iNoiseRandom, RegistryKey<Biome> northBiome, RegistryKey<Biome> westBiome, RegistryKey<Biome> southBiome, RegistryKey<Biome> eastBiome);
}
