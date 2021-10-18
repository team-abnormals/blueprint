package com.teamabnormals.blueprint.client.screen.shaking;

import net.minecraft.world.phys.Vec3;

/**
 * An interface representing a source that contributes to the intensity at which the screen shakes.
 *
 * @author SmellyModder (Luke Tonon)
 * @see ScreenShakeHandler
 */
public interface ShakeSource {
	/**
	 * Updates this source.
	 */
	void tick();

	/**
	 * Checks if this source has stopped shaking.
	 *
	 * @return If this source is no longer shaking.
	 */
	boolean isStopped();

	/**
	 * Gets the maximum X-Axis intensity this source can contribute up to.
	 * <p>Used for limiting the intensity when multiple sources are active.</p>
	 *
	 * @return The maximum X-Axis intensity this source can contribute up to.
	 */
	double getMaxBuildupX();

	/**
	 * Gets the maximum Y-Axis intensity this source can contribute up to.
	 * <p>Used for limiting the intensity when multiple sources are active.</p>
	 *
	 * @return The maximum Y-Axis intensity this source can contribute up to.
	 */
	double getMaxBuildupY();

	/**
	 * Gets the maximum Z-Axis intensity this source can contribute up to.
	 * <p>Used for limiting the intensity when multiple sources are active.</p>
	 *
	 * @return The maximum Z-Axis intensity this source can contribute up to.
	 */
	double getMaxBuildupZ();

	/**
	 * Gets the intensity of this source given the position of a camera.
	 *
	 * @param pos The position of a camera.
	 * @return The intensity of this source given the position of a camera.
	 */
	Vec3 getIntensity(Vec3 pos);
}
