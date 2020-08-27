package com.teamabnormals.abnormals_core.common.world.biome;

import net.minecraft.world.biome.Biome;

public abstract class AbnormalsBiome extends Biome {
    public AbnormalsBiome(Builder biomeBuilder) {
        super(biomeBuilder);
    }
    
    public abstract void addFeatures();

    public abstract void addSpawns();
}
