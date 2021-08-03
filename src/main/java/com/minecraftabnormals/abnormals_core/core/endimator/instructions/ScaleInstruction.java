package com.minecraftabnormals.abnormals_core.core.endimator.instructions;

import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class ScaleInstruction extends ModelRendererInstruction<ScaleInstruction> {
	private static final Codec<ScaleInstruction> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.STRING.fieldOf("model_renderer").forGetter(instruction -> instruction.modelRenderer),
			Codec.FLOAT.fieldOf("x").forGetter(instruction -> instruction.x),
			Codec.FLOAT.fieldOf("y").forGetter(instruction -> instruction.y),
			Codec.FLOAT.fieldOf("z").forGetter(instruction -> instruction.z),
			Codec.BOOL.optionalFieldOf("additive", false).forGetter(instruction -> instruction.additive)
		).apply(instance, ScaleInstruction::new);
	});
	
	public ScaleInstruction() {
		super(CODEC);
	}
	
	private ScaleInstruction(String modelRender, float x, float y, float z, boolean additive) {
		super(CODEC, modelRender, x, y, z, additive);
	}

	@Override
	public void process(EndimatorEntityModel<?> model) {
		this.cacheModelRenderer(model);
		if (this.additive) {
			model.scaleAdditive(this.cachedModelRenderer, this.x, this.y, this.z);
		} else {
			model.scale(this.cachedModelRenderer, this.x, this.y, this.z);
		}
	}
}