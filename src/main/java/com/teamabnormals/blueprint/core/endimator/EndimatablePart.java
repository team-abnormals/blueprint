package com.teamabnormals.blueprint.core.endimator;

/**
 * The interface that represents a part that can be applied by an {@link Endimator}.
 * <p>All methods are {@code default} because a part doesn't have to support every {@link KeyframeType}.</p>
 * <p>This gets mixin'd into {@link net.minecraft.client.model.geom.ModelPart}.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see Endimator
 * @see Endimator.PosedPart
 * @see EndimatorModelPart
 */
public interface EndimatablePart {
	/**
	 * Adds positional transformation values.
	 * <p>This does not always mean actual addition operations. In some systems, subtracting may visually appear as an addition.</p>
	 * <p>The values for the parameters are considered visual additions to the part.</p>
	 *
	 * @param x The x pos to add.
	 * @param y The y pos to add.
	 * @param z The z pos to add.
	 */
	default void addPos(float x, float y, float z) {
	}

	/**
	 * Adds rotational transformation values.
	 * <p>The values passed in should be measured in radians.</p>
	 *
	 * @param x The x rotation to add.
	 * @param y The y rotation to add.
	 * @param z The z rotation to add.
	 */
	default void addRotation(float x, float y, float z) {
	}

	/**
	 * Adds offset transformation values.
	 * <p>This does not always mean actual addition operations. In some systems, subtracting may visually appear as an addition.</p>
	 * <p>The values for the parameters are considered visual additions to the part.</p>
	 *
	 * @param x The x offset to add.
	 * @param y The y offset to add.
	 * @param z The z offset to add.
	 */
	default void addOffset(float x, float y, float z) {
	}

	/**
	 * Adds scale transformation values.
	 *
	 * @param x The x scale to add.
	 * @param y The y scale to add.
	 * @param z The z scale to add.
	 */
	default void addScale(float x, float y, float z) {
	}

	/**
	 * Resets the part values to their intitial values.
	 */
	default void reset() {}
}
