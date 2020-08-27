package com.teamabnormals.abnormals_core.core.library.endimator.instructions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimatorEntityModel;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class OffsetInstruction extends ModelRendererInstruction<OffsetInstruction> {
	private static final Codec<OffsetInstruction> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.STRING.fieldOf("model_renderer").forGetter(instruction -> instruction.modelRenderer),
			Codec.FLOAT.fieldOf("x").forGetter(instruction -> instruction.x),
			Codec.FLOAT.fieldOf("y").forGetter(instruction -> instruction.y),
			Codec.FLOAT.fieldOf("z").forGetter(instruction -> instruction.z),
			Codec.BOOL.optionalFieldOf("additive", false).forGetter(instruction -> instruction.additive)
		).apply(instance, OffsetInstruction::new);
	});
	
	public OffsetInstruction() {
		super(CODEC);
	}
	
	private OffsetInstruction(String modelRender, float x, float y, float z, boolean additive) {
		super(CODEC, modelRender, x, y, z, additive);
	}

	@Override
	public void process(EndimatorEntityModel<?> model) {
		this.cacheModelRenderer(model);
		if (this.additive) {
			model.offsetAdditive(this.cachedModelRenderer, this.x, this.y, this.z);
		} else {
			model.offset(this.cachedModelRenderer, this.x, this.y, this.z);
		}
	}
}