package com.teamabnormals.blueprint.common.world.modification.structure.condition;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureModificationContext;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletter;

import java.util.List;

/**
 * A {@link StructureRepaletter.Condition} implementation for requiring multiple conditions at once.
 *
 * @param conditions List of conditions to require.
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletter.Condition
 */
public record AndStructureCondition(List<StructureRepaletter.Condition> conditions) implements StructureRepaletter.Condition {
	public static final MapCodec<AndStructureCondition> CODEC = StructureRepaletter.Condition.CODEC.listOf().fieldOf("biomes").xmap(AndStructureCondition::new, AndStructureCondition::conditions);

	public static AndStructureCondition and(StructureRepaletter.Condition... conditions) {
		return new AndStructureCondition(List.of(conditions));
	}

	@Override
	public boolean test(StructureModificationContext context) {
		for (StructureRepaletter.Condition condition : this.conditions) {
			if (!condition.test(context)) return false;
		}
		return true;
	}

	@Override
	public MapCodec<? extends StructureRepaletter.Condition> codec() {
		return CODEC;
	}
}
