package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.layer.RegionHillsLayer;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RegionHillsLayer.class)
public abstract class HillsLayerMixin implements AreaTransformer2, DimensionOffset1Transformer {
	@Inject(method = "applyPixel", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/gen/INoiseRandom;nextRandom(I)I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void transformVariants(Context rand, Area area1, Area area2, int x, int z, CallbackInfoReturnable<Integer> cir, int i, int j, int k) {
		ResourceKey<Biome> hill = BiomeUtil.getHillBiome(Biomes.byId(i), rand);
		if (hill != null) {
			int l = BiomeUtil.getId(hill);

			if (k == 0 && l != i) {
				l = RegionHillsLayer.MUTATIONS.getOrDefault(l, i);
			}

			if (l != i) {
				int i1 = 0;
				int offsetX = this.getParentX(x);
				int offsetZ = this.getParentY(x);

				if (Layers.isSame(area1.get(offsetX + 1, offsetZ), i)) ++i1;
				if (Layers.isSame(area1.get(offsetX + 2, offsetZ + 1), i)) ++i1;
				if (Layers.isSame(area1.get(offsetX, offsetZ + 1), i)) ++i1;
				if (Layers.isSame(area1.get(offsetX + 1, offsetZ + 2), i)) ++i1;

				if (i1 >= 3) {
					cir.setReturnValue(l);
				}
			}
		}
	}
}