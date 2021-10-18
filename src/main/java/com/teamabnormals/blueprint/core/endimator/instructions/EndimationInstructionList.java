package com.teamabnormals.blueprint.core.endimator.instructions;

import com.teamabnormals.blueprint.core.endimator.entity.EndimatorEntityModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationInstructionList {
	public static final Codec<EndimationInstructionList> CODEC = RecordCodecBuilder.create((builder) -> {
		return builder.group(
			EndimationInstruction.CODEC.listOf().fieldOf("instructions").forGetter(list -> list.instructions)
		).apply(builder, EndimationInstructionList::new);
	});
	
	private final List<EndimationInstruction<?>> instructions;
	
	private EndimationInstructionList(List<EndimationInstruction<?>> instructions) {
		this.instructions = instructions;
	}
	
	public void processInstructions(EndimatorEntityModel<?> model) {
		this.instructions.forEach(instruction -> instruction.process(model));
	}
}