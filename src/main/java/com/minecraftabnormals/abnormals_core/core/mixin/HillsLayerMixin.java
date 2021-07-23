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
	@Inject(method = "applyPixel", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/gen/INoiseRandom;nextRandom(I)I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void transformVariants(INoiseRandom rand, IArea area1, IArea area2, int x, int z, CallbackInfoReturnable<Integer> cir, int i, int j, int k) {
		RegistryKey<Biome> hill = BiomeUtil.getHillBiome(BiomeRegistry.byId(i), rand);
		if (hill != null) {
			int l = BiomeUtil.getId(hill);

			if (k == 0 && l != i) {
				l = HillsLayer.MUTATIONS.getOrDefault(l, i);
			}

			if (l != i) {
				int i1 = 0;
				int offsetX = this.getParentX(x);
				int offsetZ = this.getParentY(x);

				if (LayerUtil.isSame(area1.get(offsetX + 1, offsetZ), i)) ++i1;
				if (LayerUtil.isSame(area1.get(offsetX + 2, offsetZ + 1), i)) ++i1;
				if (LayerUtil.isSame(area1.get(offsetX, offsetZ + 1), i)) ++i1;
				if (LayerUtil.isSame(area1.get(offsetX + 1, offsetZ + 2), i)) ++i1;

				if (i1 >= 3) {
					cir.setReturnValue(l);
				}
			}
		}
	}
}