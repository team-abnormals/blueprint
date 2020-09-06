package com.teamabnormals.abnormals_core.core.endimator;

import com.teamabnormals.abnormals_core.client.ClientInfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

/**
 * Class to make animations that can be 'controlled' during their run time
 * Can also be used as a way to make simple animations in a more clean/easy way
 * @author - SmellyModder(Luke Tonon)
 */
public final class ControlledEndimation {
	private int tick, prevTick;
	public int tickDuration;
	private boolean shouldDecrement;
	public boolean isPaused;
	
	public ControlledEndimation(int tickDuration, int startingValue) {
		this.tick = this.prevTick = startingValue;
		this.tickDuration = tickDuration;
	}
	
	/*
	 * Updates prevTick value for lerping progress
	 * Should be called before tick()
	 */
	public void update() {
		this.prevTick = this.tick;
	}
	
	/*
	 * Used to update logic and progress of the animation
	 */
	public void tick() {
		if (this.isPaused) return;
		
		if (this.shouldDecrement && this.tick > 0) {
			this.tick--;
		} else if (!this.shouldDecrement && this.tick < this.tickDuration) {
			this.tick++;
		}
	}
	
	/**
	 * Sets the timer of the animation to begin decrementing its value rather than incrementing
	 * @param shouldDecrement - Should the timer decrement
	 */
	public void setDecrementing(boolean shouldDecrement) {
		this.shouldDecrement = shouldDecrement;
	}
	
	/**
	 * @return - Is the timer decrementing
	 */
	public boolean isDecrementing() {
		return this.shouldDecrement;
	}
	
	public boolean isAtMax() {
		return this.tick == this.tickDuration;
	}
	
	public int getTick() {
		return this.tick;
	}
	
	public void setTick(int amount) {
		this.tick = this.prevTick = amount;
	}
	
	public void addTick(int amount) {
		this.tick += amount;
	}
	
	public void setTimerPaused(boolean paused) {
		this.isPaused = paused;
	}
	
	/*
	 * Resets the timer
	 */
	public void resetTimer() {
		this.tick = this.prevTick = 0;
		this.isPaused = false;
		this.shouldDecrement = false;
	}
	
	/**
	 * Gets the progress of the animation
	 * @return - a lerped value of the animation's previous tick and new tick
	 */
	public float getAnimationProgress() {
		return MathHelper.lerp(ClientInfo.getPartialTicks(), this.prevTick, this.tick) / this.tickDuration;
	}
	
	/**
	 * Gets the progress of the animation
	 * @return - a lerped value of the animation's previous tick and new tick
	 */
	public float getAnimationProgressServer() {
		return MathHelper.lerp(1.0F, this.prevTick, this.tick) / this.tickDuration;
	}
	
	/**
	 * Writes data about the ControlledEndimation to a CompoundNBT
	 * Can be used to save data about the animation when the world gets saved and for command use
	 * Values must be synced(Server -> Client)
	 */
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("Tick", this.tick);
		compound.putInt("PrevTick", this.prevTick);
		compound.putBoolean("ShouldDecrement", this.shouldDecrement);
		compound.putBoolean("IsPaused", this.isPaused);
		return compound;
	}
	
	/**
	 * Reads data about the ControlledEndimation from a CompoundNBT
	 * Can be used to save data about the animation when the world gets saved and for command use
	 * Values must be synced(Server -> Client)
	 */
	public void read(CompoundNBT nbt) {
		this.tick = nbt.getInt("Tick");
		this.prevTick = nbt.getInt("PrevTick");
		this.shouldDecrement = nbt.getBoolean("ShouldDecrement");
		this.isPaused = nbt.getBoolean("IsPaused");	
	}
}