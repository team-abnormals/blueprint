package com.minecraftabnormals.abnormals_core.core.endimator.instructions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class ResetKeyframeInstruction extends EndimationInstruction<ResetKeyframeInstruction> {
	private final int tickLength;
	
	public ResetKeyframeInstruction() {
		this(0);
	}
	
	private ResetKeyframeInstruction(int tickLength) {
		super(
			RecordCodecBuilder.create(instance -> {
				return instance.group(
					Codec.INT.fieldOf("ticks").forGetter(instruction -> instruction.tickLength)
				).apply(instance, ResetKeyframeInstruction::new);
			})
		);
		this.tickLength = tickLength;
	}

	@Override
	public void process(EndimatorEntityModel<?> model) {
		model.resetKeyframe(this.tickLength);
	}
}