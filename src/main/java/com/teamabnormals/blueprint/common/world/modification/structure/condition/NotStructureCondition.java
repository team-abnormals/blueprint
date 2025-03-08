package com.teamabnormals.blueprint.common.world.modification.structure.condition;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureModificationContext;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletter;

/**
 * A {@link StructureRepaletter.Condition} implementation that requires a condition to not be met.
 *
 * @param condition A condition to invert.
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletter.Condition
 */
public record NotStructureCondition(StructureRepaletter.Condition condition) implements StructureRepaletter.Condition {
	public static final MapCodec<NotStructureCondition> CODEC = StructureRepaletter.Condition.CODEC.fieldOf("condition").xmap(NotStructureCondition::new, NotStructureCondition::condition);

	@Override
	public boolean test(StructureModificationContext context) {
		return !this.condition.test(context);
	}

	@Override
	public MapCodec<? extends StructureRepaletter.Condition> codec() {
		return CODEC;
	}
}
