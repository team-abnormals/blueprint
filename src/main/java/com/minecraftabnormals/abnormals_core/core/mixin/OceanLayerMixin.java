package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.layer.OceanLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author ExpensiveKoala
 */
@Mixin(OceanLayer.class)
public final class OceanLayerMixin {
    @Inject(method = "apply", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/gen/ImprovedNoiseGenerator;func_215456_a(DDDDD)D", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void apply(INoiseRandom p_215735_1_, int p_215735_2_, int p_215735_3_, CallbackInfoReturnable<Integer> cir, ImprovedNoiseGenerator improvednoisegenerator, double d0) {
        BiomeUtil.OceanType type;
        if (d0 > 0.4D) {
            type = BiomeUtil.OceanType.WARM;
        } else if (d0 > 0.2D) {
            type = BiomeUtil.OceanType.LUKEWARM;
        } else if (d0 < -0.4D) {
            type = BiomeUtil.OceanType.FROZEN;
        } else if (d0 < -0.2D) {
            type = BiomeUtil.OceanType.COLD;
        } else {
            type = BiomeUtil.OceanType.NORMAL;
        }
    
        RegistryKey<Biome> biome = BiomeUtil.getOceanBiome(type, p_215735_1_);
        if (!biome.equals(Biomes.FROZEN_OCEAN) && !biome.equals(Biomes.COLD_OCEAN) && !biome.equals(Biomes.OCEAN) && !biome.equals(Biomes.LUKEWARM_OCEAN) && !biome.equals(Biomes.WARM_OCEAN)) {
            cir.setReturnValue(BiomeUtil.getId(biome));
        }
    }
}
