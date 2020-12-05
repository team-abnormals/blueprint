package com.minecraftabnormals.abnormals_core.core.endimator.instructions;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;

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