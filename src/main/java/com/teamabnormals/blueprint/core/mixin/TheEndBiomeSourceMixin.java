package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.*;
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
	@Shadow
	@Final
	private Registry<Biome> biomes;
	@Shadow
	@Final
	private Biome highlands;
	@Shadow
	@Final
	private Biome midlands;
	@Shadow
	@Final
	private Biome barrens;
	@Unique
	private Climate.ParameterList<ResourceKey<Biome>> moddedBiomes;

	private TheEndBiomeSourceMixin(List<Biome> biomes) {
		super(biomes);
	}

	@Inject(at = @At("RETURN"), method = "<init>")
	private void init(Registry<Biome> lookupRegistry, long seed, CallbackInfo info) {
		this.moddedBiomes = BiomeUtil.getEndBiomes();
	}

	@Inject(at = @At("RETURN"), method = "getNoiseBiome", cancellable = true)
	private void addEndBiomes(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Biome> info) {
		//Don't impact end biome selection performance unless there are modded end biomes
		var moddedBiomes = this.moddedBiomes;
		if (moddedBiomes.values().size() > 1) {
			Biome oldBiome = info.getReturnValue();
			if (oldBiome == this.highlands || oldBiome == this.midlands || oldBiome == this.barrens) {
				ResourceKey<Biome> biomeKey = moddedBiomes.findValue(sampler.sample(x, y, z), Biomes.THE_VOID);
				if (biomeKey != Biomes.THE_VOID) {
					info.setReturnValue(this.biomes.getOrThrow(biomeKey));
				}
			}
		}
	}
}
