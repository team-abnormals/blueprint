package com.minecraftabnormals.abnormals_core.client.screen.shaking;

import net.minecraft.util.math.vector.Vector3d;

/**
 * A {@link ShakeSource} implementation containing some values that make up a 'simple' {@link ShakeSource}.
 * <p>Ideally, this should only get used for global sources.</p>
 * <p>This class contains a timer for how long the source should last, intensity values, max build-up values, and decay values for altering the intensity over time.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see ScreenShakeHandler
 * @see SimpleShakeSource
 */
public class SimpleShakeSource implements ShakeSource {
	protected int timer;
	protected double intensityX, intensityY, intensityZ;
	protected double maxBuildupX, maxBuildupY, maxBuildupZ;
	protected double decayX, decayY, decayZ;

	public SimpleShakeSource(int duration, double intensityX, double intensityY, double intensityZ, double maxBuildupX, double maxBuildupY, double maxBuildupZ, double decayX, double decayY, double decayZ) {
		this.timer = duration;
		this.intensityX = intensityX;
		this.intensityY = intensityY;
		this.intensityZ = intensityZ;
		this.maxBuildupX = maxBuildupX;
		this.maxBuildupY = maxBuildupY;
		this.maxBuildupZ = maxBuildupZ;
		this.decayX = decayX;
		this.decayY = decayY;
		this.decayZ = decayZ;
	}

	public SimpleShakeSource(int duration, double intensity, double maxBuildup, double decay) {
		this.timer = duration;
		this.intensityX = this.intensityY = this.intensityZ = intensity;
		this.maxBuildupX = this.maxBuildupY = this.maxBuildupZ = maxBuildup;
		this.decayX = this.decayY = this.decayZ = decay;
	}

	@Override
	public void tick() {
		this.intensityX *= this.decayX;
		this.intensityY *= this.decayY;
		this.intensityZ *= this.decayZ;
		this.timer--;
	}

	@Override
	public boolean isStopped() {
		return this.timer <= 0;
	}

	@Override
	public double getMaxBuildupX() {
		return this.maxBuildupX;
	}

	@Override
	public double getMaxBuildupY() {
		return this.maxBuildupY;
	}

	@Override
	public double getMaxBuildupZ() {
		return this.maxBuildupZ;
	}

	@Override
	public Vector3d getIntensity(Vector3d pos) {
		return new Vector3d(this.intensityX, this.intensityY, this.intensityZ);
	}
}
