package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.MixOceansLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author ExpensiveKoala
 */
@Mixin(MixOceansLayer.class)
public final class MixOceansLayerMixin {
    // Inject before if statement checking if deep ocean
    @Inject(method = "applyPixel", at = @At(value = "CONSTANT", args = "intValue=24", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void applyPixel(INoiseRandom iNoiseRandom, IArea deepArea, IArea tempArea, int x, int y, CallbackInfoReturnable<Integer> cir, int i, int j) {
        // if i == deep
        if (i == 24) {
            RegistryKey<Biome> deepBiome = BiomeUtil.getDeepOceanBiome(BiomeRegistry.byId(j));
            if (deepBiome != null) {
                cir.setReturnValue(BiomeUtil.getId(deepBiome));
            }
        }
    }
}
