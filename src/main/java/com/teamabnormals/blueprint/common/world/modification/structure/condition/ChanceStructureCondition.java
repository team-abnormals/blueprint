package com.teamabnormals.blueprint.common.world.modification.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureModificationContext;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletter;

/**
 * A {@link StructureRepaletter.Condition} implementation that's true based on a chance.
 *
 * @param chance Chance for the condition to be true.
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletter.Condition
 */
public record ChanceStructureCondition(float chance) implements StructureRepaletter.Condition {
	public static final MapCodec<ChanceStructureCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
				Codec.FLOAT.fieldOf("chance").forGetter(condition -> condition.chance)
		).apply(instance, ChanceStructureCondition::new);
	});

	@Override
	public boolean test(StructureModificationContext context) {
		return context.getGenerationContext().random().nextFloat() < this.chance;
	}

	@Override
	public MapCodec<? extends StructureRepaletter.Condition> codec() {
		return CODEC;
	}
}
