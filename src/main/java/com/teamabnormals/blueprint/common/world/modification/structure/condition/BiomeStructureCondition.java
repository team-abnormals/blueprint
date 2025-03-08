package com.teamabnormals.blueprint.common.world.modification.structure.condition;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureModificationContext;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

/**
 * A {@link StructureRepaletter.Condition} implementation for requiring biomes.
 *
 * @param biomes Holder Set of biomes to require.
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletter.Condition
 */
public record BiomeStructureCondition(HolderSet<Biome> biomes) implements StructureRepaletter.Condition {
	public static final MapCodec<BiomeStructureCondition> CODEC = RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").xmap(BiomeStructureCondition::new, BiomeStructureCondition::biomes);

	@Override
	public boolean test(StructureModificationContext context) {
		return this.biomes.contains(context.biome().get());
	}

	@Override
	public MapCodec<? extends StructureRepaletter.Condition> codec() {
		return CODEC;
	}
}
