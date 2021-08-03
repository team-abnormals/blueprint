package com.minecraftabnormals.abnormals_core.core.endimator.instructions;

import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class MoveInstruction extends ModelRendererInstruction<MoveInstruction> {
	private static final Codec<MoveInstruction> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.STRING.fieldOf("model_renderer").forGetter(instruction -> instruction.modelRenderer),
			Codec.FLOAT.fieldOf("x").forGetter(instruction -> instruction.x),
			Codec.FLOAT.fieldOf("y").forGetter(instruction -> instruction.y),
			Codec.FLOAT.fieldOf("z").forGetter(instruction -> instruction.z),
			Codec.BOOL.optionalFieldOf("additive", false).forGetter(instruction -> instruction.additive)
		).apply(instance, MoveInstruction::new);
	});
	
	public MoveInstruction() {
		super(CODEC);
	}
	
	private MoveInstruction(String modelRender, float x, float y, float z, boolean additive) {
		super(CODEC, modelRender, x, y, z, additive);
	}

	@Override
	public void process(EndimatorEntityModel<?> model) {
		this.cacheModelRenderer(model);
		if (this.additive) {
			model.moveAdditive(this.cachedModelRenderer, this.x, this.y, this.z);
		} else {
			model.move(this.cachedModelRenderer, this.x, this.y, this.z);
		}
	}
}