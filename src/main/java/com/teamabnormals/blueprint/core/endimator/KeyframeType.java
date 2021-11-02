package com.teamabnormals.blueprint.core.endimator;

import com.mojang.math.Vector3f;

import java.util.function.Function;

/**
 * An enum representing the possible transformations an {@link EndimatablePart} can receive from an {@link EndimationKeyframe}.
 *
 * @author SmellyModder (Luke Tonon)
 * @see EndimatablePart
 * @see Endimation.PartKeyframes
 * @see EndimationKeyframe
 */
public enum KeyframeType {
	POSITION(Endimation.PartKeyframes::getPosFrames, Endimator.PosedPart::addPos),
	ROTATION(Endimation.PartKeyframes::getRotationFrames, Endimator.PosedPart::addRotation),
	OFFSET(Endimation.PartKeyframes::getOffsetFrames, Endimator.PosedPart::addOffset),
	SCALE(Endimation.PartKeyframes::getScaleFrames, Endimator.PosedPart::addScale);

	private final Function<Endimation.PartKeyframes, EndimationKeyframe[]> getter;
	private final Procedure procedure;

	KeyframeType(Function<Endimation.PartKeyframes, EndimationKeyframe[]> getter, Procedure procedure) {
		this.getter = getter;
		this.procedure = procedure;
	}

	/**
	 * Gets the array of {@link EndimationKeyframe} instances associated with this type for a given {@link Endimation.PartKeyframes} instance.
	 *
	 * @param partKeyframes A {@link Endimation.PartKeyframes} instance to get the array of {@link EndimationKeyframe} instances from.
	 * @return The array of {@link EndimationKeyframe} instances associated with this type for a given {@link Endimation.PartKeyframes} instance.
	 */
	public EndimationKeyframe[] getFrames(Endimation.PartKeyframes partKeyframes) {
		return this.getter.apply(partKeyframes);
	}

	/**
	 * Applies a given transformation {@link Vector3f} to a {@link Endimator.PosedPart} using this type's {@link #procedure}.
	 *
	 * @param pose     A {@link Endimator.PosedPart} to apply to.
	 * @param vector3f A transformation {@link Vector3f} to apply.
	 * @see Endimator#apply(Endimation, float, Endimator.ResetMode)
	 */
	public void apply(Endimator.PosedPart pose, Vector3f vector3f) {
		this.procedure.apply(pose, vector3f.x(), vector3f.y(), vector3f.z());
	}

	@FunctionalInterface
	interface Procedure {
		void apply(Endimator.PosedPart pose, float x, float y, float z);
	}
}
