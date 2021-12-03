package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.gen.BlueprintLayerUtil;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TheEndBiomeSource.class)
public abstract class TheEndBiomeSourceMixin extends BiomeSource {
//	@Shadow
//	@Final
//	private Registry<Biome> biomes;
//	@Shadow
//	@Final
//	private Biome highlands;
//	@Shadow
//	@Final
//	private Biome midlands;
//	@Shadow
//	@Final
//	private Biome barrens;
//	@Unique
//	private Layer noiseBiomeLayer;
//
	private TheEndBiomeSourceMixin(List<Biome> biomes) {
		super(biomes);
	}
//
//	@Inject(at = @At("RETURN"), method = "<init>")
//	private void init(Registry<Biome> lookupRegistry, long seed, CallbackInfo info) {
//		this.noiseBiomeLayer = BlueprintLayerUtil.createEndBiomeLayer(this.biomes, (seedModifier) -> new LazyAreaContext(25, seed, seedModifier));
//	}
//
//	@Inject(at = @At("RETURN"), method = "getNoiseBiome(III)Lnet/minecraft/world/level/biome/Biome;", cancellable = true)
//	private void addEndBiomes(int x, int y, int z, CallbackInfoReturnable<Biome> info) {
//		Biome oldBiome = info.getReturnValue();
//		if (oldBiome == this.highlands || oldBiome == this.midlands || oldBiome == this.barrens) {
//			Biome newBiome = this.getNoiseBiome(x, z);
//			if (newBiome != this.midlands) {
//				info.setReturnValue(newBiome);
//			}
//		}
//	}
//
//	private Biome getNoiseBiome(int x, int z) {
//		int biomeID = this.noiseBiomeLayer.area.get(x, z);
//		Biome biome = this.biomes.byId(biomeID);
//		if (biome == null) {
//			Blueprint.LOGGER.warn("Unknown end biome id: {}", biomeID);
//			return this.midlands;
//		}
//		return biome;
//	}
}
