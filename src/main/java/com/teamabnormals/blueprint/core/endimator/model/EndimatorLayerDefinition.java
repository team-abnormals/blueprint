package com.teamabnormals.blueprint.core.endimator.model;

import com.teamabnormals.blueprint.core.endimator.EndimatorModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

/**
 * A slightly hacky subclass of {@link LayerDefinition} to ease creation of {@link EndimatorModelPart} trees.
 *
 * @author SmellyModder
 */
public final class EndimatorLayerDefinition extends LayerDefinition {
	private final EndimatorPartDefinition root;
	private final int xTexSize;
	private final int yTexSize;

	public EndimatorLayerDefinition(EndimatorPartDefinition root, int xTexSize, int yTexSize) {
		super(null, null);
		this.root = root;
		this.xTexSize = xTexSize;
		this.yTexSize = yTexSize;
	}

	@Override
	public EndimatorModelPart bakeRoot() {
		return this.root.bake(this.xTexSize, this.yTexSize);
	}
}
