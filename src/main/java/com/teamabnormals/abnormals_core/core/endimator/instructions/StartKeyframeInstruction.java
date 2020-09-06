package com.teamabnormals.abnormals_core.core.endimator.instructions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class StartKeyframeInstruction extends EndimationInstruction<StartKeyframeInstruction> {
	private final int tickLength;
	
	public StartKeyframeInstruction() {
		this(0);
	}
	
	private StartKeyframeInstruction(int tickLength) {
		super(
			RecordCodecBuilder.create(instance -> {
				return instance.group(
					Codec.INT.fieldOf("ticks").forGetter(instruction -> instruction.tickLength)
				).apply(instance, StartKeyframeInstruction::new);
			})
		);
		this.tickLength = tickLength;
	}

	@Override
	public void process(EndimatorEntityModel<?> model) {
		model.startKeyframe(this.tickLength);
	}
}