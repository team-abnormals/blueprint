package com.minecraftabnormals.abnormals_core.core.api;

import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.client.renderer.Sheets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

public final class SignManager {
    private static final Set<WoodType> WOOD_TYPES = new HashSet<>();

    @OnlyIn(Dist.CLIENT)
    public static void setupAtlas() {
        for (WoodType type : WOOD_TYPES)
            Sheets.addWoodType(type);
    }

    public static synchronized WoodType registerWoodType(WoodType type) {
        WOOD_TYPES.add(type);
        return WoodType.register(type);
    }
}
