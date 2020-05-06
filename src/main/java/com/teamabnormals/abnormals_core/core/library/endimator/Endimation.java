package com.teamabnormals.abnormals_core.core.library.endimator;

/**
 * A class that tells how long an animation will play and if it's looped
 * @author - SmellyModder(Luke Tonon) 
 */
public class Endimation {
	private int tickDuration;
	
	/**
	 * Simple constructor for an Animation
	 * @param tickDuration - Duration of how long the animation plays for; measured in ticks
	 */
	public Endimation(int tickDuration) {
		this.tickDuration = tickDuration;
	}
	
	/**
	 * Empty constructor; used for making animations that do nothing
	 */
	public Endimation() {
		this(0);
	}
	
	/**
	 * @return - The duration of this animation; measured in ticks
	 */
	public int getAnimationTickDuration() {
		return this.tickDuration;
	}
}