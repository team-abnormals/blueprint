package com.minecraftabnormals.abnormals_core.core.endimator.instructions;

import com.mojang.serialization.Codec;
import com.minecraftabnormals.abnormals_core.core.endimator.EndimationDataManager;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;

/**
 * @author SmellyModder (Luke Tonon)
 *
 * @param <IS> - The type of {@link EndimationInstruction}.
 */
public abstract class EndimationInstruction<IS extends EndimationInstruction<?>> {
	public static final Codec<EndimationInstruction<?>> CODEC = EndimationInstructions.REGISTRY.dispatchStable(instruction -> instruction, EndimationInstruction::codecFor);
	private final Codec<IS> codec;
	
	public EndimationInstruction(Codec<IS> codec) {
		this.codec = codec;
	}

	/**
	 * Call when processing the instruction on a {@link EndimatorEntityModel}.
	 * @param model - The model to process the instruction for.
	 */
	public abstract void process(EndimatorEntityModel<?> model);

	/**
	 * @return The Codec for deserializing this instruction in {@link EndimationDataManager}.
	 */
	public Codec<IS> getCodec() {
		return this.codec;
	}
	
	@SuppressWarnings("unchecked")
	private static <C extends EndimationInstruction<?>> Codec<C> codecFor(C instruction) {
		return (Codec<C>) instruction.getCodec();
	}
}