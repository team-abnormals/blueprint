package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.ShoreLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author ExpensiveKoala
 */
@Mixin(ShoreLayer.class)
public final class ShoreLayerMixin {
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void apply(INoiseRandom context, int north, int west, int south, int east, int center, CallbackInfoReturnable<Integer> cir) {
        RegistryKey<Biome> biome = BiomeUtil.getEdgeBiome(BiomeRegistry.getKeyFromID(center), context, BiomeRegistry.getKeyFromID(north), BiomeRegistry.getKeyFromID(west), BiomeRegistry.getKeyFromID(south), BiomeRegistry.getKeyFromID(east));
        
        if (biome != null) {
            cir.setReturnValue(BiomeUtil.getId(biome));
        }
    }
}
