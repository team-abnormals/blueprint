package com.teamabnormals.abnormals_core.core.library.endimator.instructions;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.EndimatorEntityModel;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class EndKeyframeInstruction extends EndimationInstruction<EndKeyframeInstruction> {
	private Unit unit;
	
	public EndKeyframeInstruction() {
		this(Unit.INSTANCE);
	}
	
	private EndKeyframeInstruction(Unit unit) {
		super(
			RecordCodecBuilder.create(instance -> {
				return instance.group(
					Codec.EMPTY.orElse(Unit.INSTANCE).forGetter(instruction -> instruction.unit)
				).apply(instance, EndKeyframeInstruction::new);
			})
		);
		this.unit = unit;
	}

	@Override
	public void process(EndimatorEntityModel<?> model) {
		model.endKeyframe();
	}
}