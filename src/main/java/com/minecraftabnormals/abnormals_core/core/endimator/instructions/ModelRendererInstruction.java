package com.minecraftabnormals.abnormals_core.core.endimator.instructions;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorModelRenderer;

/**
 * @author SmellyModder (Luke Tonon)
 */
public abstract class ModelRendererInstruction<IS extends EndimationInstruction<?>> extends EndimationInstruction<IS> {
	protected final String modelRenderer;
	protected final float x, y, z;
	protected final boolean additive;
	protected EndimatorModelRenderer cachedModelRenderer;
	
	protected ModelRendererInstruction(Codec<IS> codec) {
		this(codec, "", 0.0F, 0.0F, 0.0F, false);
	}
	
	protected ModelRendererInstruction(Codec<IS> codec, String modelRenderer, float x, float y, float z, boolean additive) {
		super(codec);
		this.modelRenderer = modelRenderer;
		this.x = x;
		this.y = y;
		this.z = z;
		this.additive = additive;
	}
	
	protected void cacheModelRenderer(EndimatorEntityModel<?> model) {
		if (this.cachedModelRenderer == null) {
			this.cachedModelRenderer = this.getModelRendererByName(model, this.modelRenderer);
		}
	}
	
	@Nonnull
	private EndimatorModelRenderer getModelRendererByName(EndimatorEntityModel<?> model, String name) {
		List<EndimatorModelRenderer> boxes = model.savedBoxes;
		for (EndimatorModelRenderer box : boxes) {
			if (box.getName().equals(name)) {
				return box;
			}
		}
		throw new NullPointerException("Could not find EndimatorModelRenderer with name: " + name);
	}
}