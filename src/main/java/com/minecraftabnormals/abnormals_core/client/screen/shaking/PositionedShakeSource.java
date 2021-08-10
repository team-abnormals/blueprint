package com.minecraftabnormals.abnormals_core.client.screen.shaking;

import net.minecraft.util.math.vector.Vector3d;

/**
 * An extension of {@link SimpleShakeSource} applied to 3D space.
 * <p>This class includes all the features of {@link SimpleShakeSource} with the addition of it having a 3D position.</p>
 * <p>The shake intensity will decrease <b>exponentially</b> the farther away the camera is from the position.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see SimpleShakeSource
 * @see ScreenShakeHandler
 */
public class PositionedShakeSource extends SimpleShakeSource {
	protected double x, y, z;

	public PositionedShakeSource(int duration, double intensityX, double intensityY, double intensityZ, double maxBuildupX, double maxBuildupY, double maxBuildupZ, double decayX, double decayY, double decayZ, double x, double y, double z) {
		super(duration, intensityX, intensityY, intensityZ, maxBuildupX, maxBuildupY, maxBuildupZ, decayX, decayY, decayZ);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PositionedShakeSource(int duration, double intensity, double maxBuildup, double decay, double x, double y, double z) {
		super(duration, intensity, maxBuildup, decay);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Vector3d getIntensity(Vector3d pos) {
		double dx = this.x - pos.x;
		double dy = this.y - pos.y;
		double dz = this.z - pos.z;
		double distanceFactor = 0.001F * (dx * dx + dy * dy + dz * dz);
		return new Vector3d(Math.max(0, this.intensityX - distanceFactor), Math.max(0, this.intensityY - distanceFactor), Math.max(0, this.intensityZ - distanceFactor));
	}
}
