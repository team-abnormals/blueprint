package com.teamabnormals.abnormals_core.common.world.biome;

import net.minecraft.world.biome.Biome;

public abstract class AbnormalsBiome extends Biome {
    
    protected AbnormalsBiome(Builder biomeBuilder) {
        super(biomeBuilder);
    }
    
    public void addFeatures() {}
    public void addSpawns() {}
}
