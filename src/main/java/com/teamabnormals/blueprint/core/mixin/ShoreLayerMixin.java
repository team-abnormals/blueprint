package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.ShoreLayer;
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
    private void apply(Context context, int north, int west, int south, int east, int center, CallbackInfoReturnable<Integer> cir) {
        ResourceKey<Biome> biome = BiomeUtil.getEdgeBiome(Biomes.byId(center), context, Biomes.byId(north), Biomes.byId(west), Biomes.byId(south), Biomes.byId(east));
        
        if (biome != null) {
            cir.setReturnValue(BiomeUtil.getId(biome));
        }
    }
}
