package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.HillsLayer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HillsLayer.class)
public abstract class HillsLayerMixin implements IAreaTransformer2, IDimOffset1Transformer {
	@Inject(method = "apply", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/gen/INoiseRandom;random(I)I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void transformVariants(INoiseRandom rand, IArea area1, IArea area2, int x, int z, CallbackInfoReturnable<Integer> cir, int i, int j, int k) {
		RegistryKey<Biome> hill = BiomeUtil.getHillBiome(BiomeRegistry.getKeyFromID(i), rand);
		if (hill != null) {
			int l = BiomeUtil.getId(hill);

			if (k == 0 && l != i) {
				l = HillsLayer.field_242940_c.getOrDefault(l, i);
			}

			if (l != i) {
				int i1 = 0;
				int offsetX = this.getOffsetX(x);
				int offsetZ = this.getOffsetZ(x);

				if (LayerUtil.areBiomesSimilar(area1.getValue(offsetX + 1, offsetZ), i)) ++i1;
				if (LayerUtil.areBiomesSimilar(area1.getValue(offsetX + 2, offsetZ + 1), i)) ++i1;
				if (LayerUtil.areBiomesSimilar(area1.getValue(offsetX, offsetZ + 1), i)) ++i1;
				if (LayerUtil.areBiomesSimilar(area1.getValue(offsetX + 1, offsetZ + 2), i)) ++i1;

				if (i1 >= 3) {
					cir.setReturnValue(l);
				}
			}
		}
	}
}