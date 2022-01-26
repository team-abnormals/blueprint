package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.List;

/**
 * The simple record class for representing a configured modifier that adds a {@link BiomeUtil.ModdedBiomeProvider} instance for a specific list of level targets.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
//TODO: Add built-in data generators
public record BiomeSourceModifier(List<ResourceKey<LevelStem>> targets, BiomeUtil.ModdedBiomeProvider provider) {
	public static final Codec<BiomeSourceModifier> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				ResourceLocation.CODEC.listOf().fieldOf("targets").xmap(resourceLocations -> resourceLocations.stream().map(location -> ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, location)).toList(), resourceKeys -> resourceKeys.stream().map(ResourceKey::location).toList()).forGetter(BiomeSourceModifier::targets),
				BiomeUtil.ModdedBiomeProvider.CODEC.fieldOf("provider").forGetter(modifier -> modifier.provider)
		).apply(instance, BiomeSourceModifier::new);
	});
}
