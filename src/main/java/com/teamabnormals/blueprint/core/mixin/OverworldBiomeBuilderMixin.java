package com.teamabnormals.blueprint.core.mixin;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public final class OverworldBiomeBuilderMixin {
	@Shadow
	@Final
	private Climate.Parameter FULL_RANGE;
	@Shadow
	@Final
	private Climate.Parameter deepOceanContinentalness;
	@Shadow
	@Final
	private Climate.Parameter oceanContinentalness;

	@Inject(at = @At("RETURN"), method = "addOffCoastBiomes")
	private void addModdedOffCoastBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> list) {
		Climate.Parameter deepOceanContinentalness = this.deepOceanContinentalness;
		Climate.Parameter oceanContinentalness = this.oceanContinentalness;
		Climate.Parameter fullRange = this.FULL_RANGE;
		for (var pair : BiomeUtil.getOceanBiomes()) {
			Climate.Parameter parameter = pair.getFirst();
			var oceanBiomes = pair.getSecond();
			this.addSurfaceBiome(list, parameter, fullRange, deepOceanContinentalness, fullRange, fullRange, 0.0F, oceanBiomes.getSecond());
			this.addSurfaceBiome(list, parameter, fullRange, oceanContinentalness, fullRange, fullRange, 0.0F, oceanBiomes.getFirst());
		}
	}

	@Shadow
	private void addSurfaceBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> p_187181_, Climate.Parameter p_187182_, Climate.Parameter p_187183_, Climate.Parameter p_187184_, Climate.Parameter p_187185_, Climate.Parameter p_187186_, float p_187187_, ResourceKey<Biome> p_187188_) {
	}
}
