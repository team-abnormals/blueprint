package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.world.level.newbiome.layer.Layers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author ExpensiveKoala
 */
@Mixin(Layers.class)
public final class LayersMixin {
    @Inject(method = "isOcean", at = @At("HEAD"), cancellable = true)
    private static void isOcean(int biomeIn, CallbackInfoReturnable<Boolean> cir) {
        ResourceKey<Biome> biome = Biomes.byId(biomeIn);
        if (BiomeUtil.isOceanBiome(biome)) {
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "isShallowOcean", at = @At("HEAD"), cancellable = true)
    private static void isShallowOcean(int biomeIn, CallbackInfoReturnable<Boolean> cir) {
        ResourceKey<Biome> biome = Biomes.byId(biomeIn);
        if (BiomeUtil.isShallowOceanBiome(biome)) {
            cir.setReturnValue(true);
        }
    }
}
