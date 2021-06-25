package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.common.world.gen.EdgeBiomeProvider;
import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.ShoreLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(ShoreLayer.class)
public class ShoreLayerMixin {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void apply(INoiseRandom context, int north, int west, int south, int east, int center, CallbackInfoReturnable<Integer> cir) {
        EdgeBiomeProvider edgeBiomeProvider = BiomeUtil.getEdgeBiomeProvider(BiomeRegistry.getKeyFromID(center));
        if(edgeBiomeProvider == null) {
            return;
        }
        RegistryKey<Biome> biome = edgeBiomeProvider.getEdgeBiome(context, BiomeRegistry.getKeyFromID(north), BiomeRegistry.getKeyFromID(west), BiomeRegistry.getKeyFromID(south), BiomeRegistry.getKeyFromID(east));
        if(biome != null) {
            cir.setReturnValue(getId(biome));
        }
    }
    
    @SuppressWarnings("deprecation")
    private int getId(@Nonnull RegistryKey<Biome> biome) {
        return WorldGenRegistries.BIOME.getId(WorldGenRegistries.BIOME.getValueForKey(biome));
    }
}
