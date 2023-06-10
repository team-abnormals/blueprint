package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * The record class for representing a weighted slice of the world that uses a {@link BiomeUtil.ModdedBiomeProvider} instance for selecting biomes.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ModdedBiomeSlice(HolderSet<LevelStem> levels, int weight, BiomeUtil.ModdedBiomeProvider provider) {
	public static final Codec<ModdedBiomeSlice> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				RegistryCodecs.homogeneousList(Registries.LEVEL_STEM).fieldOf("levels").forGetter(slice -> slice.levels),
				ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(slice -> slice.weight),
				BiomeUtil.ModdedBiomeProvider.CODEC.fieldOf("provider").forGetter(slice -> slice.provider)
		).apply(instance, ModdedBiomeSlice::new);
	});
}
