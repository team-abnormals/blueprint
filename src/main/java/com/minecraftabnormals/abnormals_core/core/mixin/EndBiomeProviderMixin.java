package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.common.world.gen.ACLayerUtil;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.layer.Layer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndBiomeProvider.class)
public abstract class EndBiomeProviderMixin {
	@Shadow
	@Final
	private Registry<Biome> lookupRegistry;
	@Shadow
	@Final
	private Biome endMidlandsBiome;
	@Shadow
	@Final
	private Biome endBarrensBiome;
	@Shadow
	@Final
	private Biome endHighlandsBiome;
	
	private Layer noiseBiomeLayer;
	
	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/util/registry/Registry;JLnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;)V")
	private void init(Registry<Biome> lookupRegistry, long seed, Biome theEndBiome, Biome endHighlandsBiome, Biome endMidlandsBiome, Biome smallEndIslandsBiome, Biome endBarrensBiome, CallbackInfo ci) {
		this.noiseBiomeLayer = ACLayerUtil.createEndBiomeLayer(lookupRegistry, (seedModifier) -> new LazyAreaLayerContext(25, seed, seedModifier));
	}
	
	@Inject(at = @At("RETURN"), method = "getNoiseBiome(III)Lnet/minecraft/world/biome/Biome;", cancellable = true)
	private void addEndBiomes(int x, int y, int z, CallbackInfoReturnable<Biome> info) {
		if (
				info.getReturnValue().equals(endHighlandsBiome) ||
						info.getReturnValue().equals(endMidlandsBiome) ||
						info.getReturnValue().equals(endBarrensBiome)
		) {
			Biome biome = getNoiseBiome(x, z);
			boolean isChorus = biome == lookupRegistry.getOrDefault(new ResourceLocation("minecraft:end_midlands"));
			if (!isChorus) {
				info.setReturnValue(biome);
			}
		}
	}
	
	private Biome getNoiseBiome(int x, int z) {
		int biomeID = this.noiseBiomeLayer.field_215742_b.getValue(x, z);
		Biome biome = this.lookupRegistry.getByValue(biomeID);
		if (biome == null) {
			AbnormalsCore.LOGGER.warn("Unknown end biome id: {}", biomeID);
			return this.endMidlandsBiome;
		}
		return biome;
	}
}
