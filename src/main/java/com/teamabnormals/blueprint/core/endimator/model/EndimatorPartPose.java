package com.teamabnormals.blueprint.core.endimator.model;

import net.minecraft.client.model.geom.PartPose;

/**
 * Similar to {@link PartPose} but includes additional fields for storing offset and scale values.
 * <p>This class works kinda like a builder, but with less boilerplate.</p>
 *
 * @author SmellyModder
 */
public final class EndimatorPartPose {
	public PartPose partPose = PartPose.ZERO;
	public float offsetX;
	public float offsetY;
	public float offsetZ;
	public float scaleX = 1.0F;
	public float scaleY = 1.0F;
	public float scaleZ = 1.0F;
	public boolean scaleChildren = true;

	/**
	 * Sets the {@link #partPose}.
	 *
	 * @param partPose A {@link PartPose} instance to use.
	 * @return This pose.
	 */
	public EndimatorPartPose setPartPose(PartPose partPose) {
		this.partPose = partPose;
		return this;
	}

	/**
	 * Sets the offset values.
	 *
	 * @param offsetX The x offset value.
	 * @param offsetY The y offset value.
	 * @param offsetZ The z offset value.
	 * @return This pose.
	 */
	public EndimatorPartPose setOffset(float offsetX, float offsetY, float offsetZ) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		return this;
	}

	/**
	 * Sets the scale values.
	 *
	 * @param scaleX The x scale value.
	 * @param scaleY The y scale value.
	 * @param scaleZ The z scale value.
	 * @return This pose.
	 */
	public EndimatorPartPose setScale(float scaleX, float scaleY, float scaleZ) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		return this;
	}

	/**
	 * Sets if this pose should scale its children.
	 *
	 * @param scaleChildren If this pose should scale its children.
	 * @return This pose.
	 */
	public EndimatorPartPose setScaleChildren(boolean scaleChildren) {
		this.scaleChildren = scaleChildren;
		return this;
	}
}
