package com.teamabnormals.abnormals_core.core.library.endimator;

import java.util.Objects;

import javax.annotation.Nullable;

import com.teamabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * A class that holds information on an animation
 * @author - SmellyModder(Luke Tonon) 
 */
public class Endimation {
	private int tickDuration;
	@Nullable
	private final ResourceLocation instructionsLocation;
	
	/**
	 * Data Driven constructor for an Animation
	 * @param tickDuration - Duration of how long the animation plays for; measured in ticks
	 * @param instructionsLocation - The location of the JSON instructions for this animation
	 */
	public Endimation(int tickDuration, ResourceLocation instructionsLocation) {
		this.tickDuration = tickDuration;
		this.instructionsLocation = instructionsLocation;
	}
	
	/**
	 * Simple constructor for an Animation
	 * @param tickDuration - Duration of how long the animation plays for; measured in ticks
	 */
	public Endimation(int tickDuration) {
		this(tickDuration, null);
	}
	
	/**
	 * Empty constructor; used for making animations that do nothing
	 */
	public Endimation() {
		this(0, null);
	}
	
	/**
	 * Processes the Instructions for this animation from a JSON file
	 * @param modelRenderer - The model to process the animation for
	 */
	@Deprecated
	public <E extends Entity & IEndimatedEntity> void processInstructions(EndimatorEntityModel<E> modelRenderer) {
		Objects.requireNonNull(this.instructionsLocation, () -> "Instructions are null, this should not be the case!");
	}
	
	/**
	 * @return - The duration of this animation; measured in ticks
	 */
	public int getAnimationTickDuration() {
		return this.tickDuration;
	}
}