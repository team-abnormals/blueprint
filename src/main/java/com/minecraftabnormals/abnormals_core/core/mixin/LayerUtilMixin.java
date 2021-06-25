package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.layer.LayerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LayerUtil.class)
public final class LayerUtilMixin {
    @Inject(method = "isOcean", at = @At("HEAD"), cancellable = true)
    private static void isOcean(int biomeIn, CallbackInfoReturnable<Boolean> cir) {
        RegistryKey<Biome> biome = BiomeRegistry.getKeyFromID(biomeIn);
        if (BiomeUtil.isOceanBiome(biome)) {
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "isShallowOcean", at = @At("HEAD"), cancellable = true)
    private static void isShallowOcean(int biomeIn, CallbackInfoReturnable<Boolean> cir) {
        RegistryKey<Biome> biome = BiomeRegistry.getKeyFromID(biomeIn);
        if (BiomeUtil.isShallowOceanBiome(biome)) {
            cir.setReturnValue(true);
        }
    }
}
