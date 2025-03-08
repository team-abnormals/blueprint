package com.teamabnormals.blueprint.common.world.modification.structure.condition;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureModificationContext;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletter;

import java.util.List;

/**
 * A {@link StructureRepaletter.Condition} implementation requiring at least one of multiple conditions to be true.
 *
 * @param conditions List of conditions to require.
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletter.Condition
 */
public record OrStructureCondition(List<StructureRepaletter.Condition> conditions) implements StructureRepaletter.Condition {
	public static final MapCodec<OrStructureCondition> CODEC = StructureRepaletter.Condition.CODEC.listOf().fieldOf("biomes").xmap(OrStructureCondition::new, OrStructureCondition::conditions);

	public static OrStructureCondition or(StructureRepaletter.Condition... conditions) {
		return new OrStructureCondition(List.of(conditions));
	}

	@Override
	public boolean test(StructureModificationContext context) {
		for (StructureRepaletter.Condition condition : this.conditions) {
			if (condition.test(context)) return true;
		}
		return false;
	}

	@Override
	public MapCodec<? extends StructureRepaletter.Condition> codec() {
		return CODEC;
	}
}
