package com.teamabnormals.abnormals_core.core.library.endimator.instructions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimatorEntityModel;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class StaticKeyframeInstruction extends EndimationInstruction<StaticKeyframeInstruction> {
	private final int tickLength;
	
	public StaticKeyframeInstruction() {
		this(0);
	}
	
	private StaticKeyframeInstruction(int tickLength) {
		super(
			RecordCodecBuilder.create(instance -> {
				return instance.group(
					Codec.INT.fieldOf("ticks").forGetter(instruction -> instruction.tickLength)
				).apply(instance, StaticKeyframeInstruction::new);
			})
		);
		this.tickLength = tickLength;
	}
	
	@Override
	public void process(EndimatorEntityModel<?> model) {
		model.setStaticKeyframe(this.tickLength);
	}
}