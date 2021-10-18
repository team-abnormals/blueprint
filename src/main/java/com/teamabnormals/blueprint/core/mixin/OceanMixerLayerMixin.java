package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.layer.OceanMixerLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author ExpensiveKoala
 */
@Mixin(OceanMixerLayer.class)
public final class OceanMixerLayerMixin {
    // Inject before if statement checking if deep ocean
    @Inject(method = "applyPixel", at = @At(value = "CONSTANT", args = "intValue=24", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void applyPixel(Context iNoiseRandom, Area deepArea, Area tempArea, int x, int y, CallbackInfoReturnable<Integer> cir, int i, int j) {
        // if i == deep
        if (i == 24) {
            ResourceKey<Biome> deepBiome = BiomeUtil.getDeepOceanBiome(Biomes.byId(j));
            if (deepBiome != null) {
                cir.setReturnValue(BiomeUtil.getId(deepBiome));
            }
        }
    }
}
