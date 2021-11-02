package com.teamabnormals.blueprint.core.endimator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import java.util.function.Function;

/**
 * A class that eases the creation of basic animations that require dynamic control.
 * <p>This class also has a {@link #read(CompoundTag)} and a {@link #write(CompoundTag)} to ease the serialization and deserialization of dynamic animations.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class TimedEndimation {
	private int duration;
	private int tick;
	private boolean decrementing;
	private boolean paused;

	public TimedEndimation(int duration, int startingValue) {
		this.duration = duration;
		this.tick = startingValue;
	}

	/**
	 * Updates the internal timer of the animation.
	 */
	public void tick() {
		if (this.paused) return;
		boolean decrementing = this.decrementing;
		int tick = this.tick;
		if (decrementing && tick > 0) {
			this.tick--;
		} else if (!decrementing && tick < this.duration) {
			this.tick++;
		}
	}

	/**
	 * Checks if the timer is counting down.
	 *
	 * @return If the timer is counting down
	 */
	public boolean isDecrementing() {
		return this.decrementing;
	}

	/**
	 * Sets if the timer of the animation should count down or up.
	 *
	 * @param decrementing Should the timer count down.
	 */
	public void setDecrementing(boolean decrementing) {
		this.decrementing = decrementing;
	}

	/**
	 * Checks if the timer has counted up to the {@link #duration}.
	 *
	 * @return If the timer has counted up to the {@link #duration}.
	 */
	public boolean isMaxed() {
		return this.tick >= this.duration;
	}

	/**
	 * Gets the {@link #tick} the timer is at.
	 *
	 * @return The {@link #tick} the timer is at.
	 */
	public int getTick() {
		return this.tick;
	}

	/**
	 * Sets the {@link #tick} the timer should be at.
	 *
	 * @param tick The new {@link #tick} the timer will be at.
	 */
	public void setTick(int tick) {
		this.tick = tick;
	}

	/**
	 * Adds ticks to the timer.
	 *
	 * @param amount The amount of ticks to add.
	 */
	public void addTick(int amount) {
		this.tick += amount;
	}

	/**
	 * Gets the timer's {@link #duration}.
	 *
	 * @return The timer's {@link #duration}.
	 */
	public int getDuration() {
		return this.duration;
	}

	/**
	 * Sets the timer's {@link #duration}.
	 *
	 * @param duration The new duration.
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * Checks if the timer is paused.
	 *
	 * @return If the timer is paused.
	 */
	public boolean isPaused() {
		return this.paused;
	}

	/**
	 * Sets if the timer should be paused.
	 *
	 * @param paused If the timer should be paused.
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * Reset the timer.
	 * <p>The timer will be reset to 0, unpaused, and begin counting up.</p>
	 */
	public void reset() {
		this.tick = 0;
		this.paused = false;
		this.decrementing = false;
	}

	/**
	 * Gets the linear client-side progress of the animation.
	 * <p>This method is not safe to call on the server-side.</p>
	 *
	 * @return The linear progress of the animation.
	 */
	public float getProgress(float partialTicks) {
		float tick = this.tick;
		if (!this.paused) {
			tick = this.decrementing ? (this.tick - partialTicks) : (this.tick + partialTicks);
		}
		return Mth.clamp(tick / this.duration, 0.0F, 1.0F);
	}

	/**
	 * Gets the client-side progress of the animation using a given easing function.
	 * <p>This method is not safe to call on the server-side.</p>
	 * <p>The easing function should only input and output values between 0 and 1.</p>
	 *
	 * @return The progress of the animation computed by a given easing function.
	 * @see com.teamabnormals.blueprint.core.endimator.interpolation.EndimationEasers
	 */
	public float getProgress(Function<Float, Float> easing, float partialTicks) {
		return easing.apply(this.getProgress(partialTicks));
	}

	/**
	 * Gets the linear progress of the animation.
	 *
	 * @return The linear progress of the animation.
	 */
	public float getServerProgress() {
		return this.getProgress(0.0F);
	}

	/**
	 * Gets the progress of the animation using a given easing function.
	 * <p>The easing function should only input and output values between 0 and 1.</p>
	 *
	 * @return The progress of the animation computed by a given easing function.
	 * @see com.teamabnormals.blueprint.core.endimator.interpolation.EndimationEasers
	 */
	public float getServerProgress(Function<Float, Float> easing) {
		return easing.apply(this.getServerProgress());
	}

	/**
	 * Writes data about the animation to a given {@link CompoundTag}.
	 *
	 * @return The given {@link CompoundTag} with data about the animation.
	 */
	public CompoundTag write(CompoundTag tag) {
		tag.putInt("Tick", this.tick);
		tag.putBoolean("Decrementing", this.decrementing);
		tag.putBoolean("Paused", this.paused);
		return tag;
	}

	/**
	 * Reads data about the animation from a given {@link CompoundTag}.
	 *
	 * @param tag A {@link CompoundTag} to read from.
	 */
	public void read(CompoundTag tag) {
		this.tick = tag.getInt("Tick");
		this.decrementing = tag.getBoolean("Decrementing");
		this.paused = tag.getBoolean("Paused");
	}
}