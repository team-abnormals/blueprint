package com.teamabnormals.abnormals_core.core.endimator;

import java.util.Objects;

import javax.annotation.Nullable;

import com.teamabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;
import com.teamabnormals.abnormals_core.core.endimator.entity.IEndimatedEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * A class that holds information for an animation
 *
 * @author - SmellyModder(Luke Tonon)
 */
public class Endimation {
	@Nullable
	private final ResourceLocation instructionsLocation;
	private int tickDuration;

	/**
	 * Data Driven constructor for an Endimation
	 *
	 * @param tickDuration         - Duration of how long the animation plays for; measured in ticks.
	 * @param instructionsLocation - The location of the JSON instructions for this animation.
	 */
	public Endimation(ResourceLocation instructionsLocation, int tickDuration) {
		this.instructionsLocation = instructionsLocation;
		this.tickDuration = tickDuration;
	}

	/**
	 * Simple constructor for an Endimation.
	 *
	 * @param tickDuration - Duration of how long the animation plays for; measured in ticks.
	 */
	public Endimation(int tickDuration) {
		this(null, tickDuration);
	}

	/**
	 * Empty constructor; used for making Endimations that do nothing.
	 */
	public Endimation() {
		this(0);
	}

	/**
	 * Processes the Instructions for this animation.
	 *
	 * @param model - The model to process the animation for.
	 */
	public <E extends Entity & IEndimatedEntity> void processInstructions(EndimatorEntityModel<E> model) {
		Objects.requireNonNull(this.instructionsLocation, "Instructions are null, this should not be the case!");
		EndimationDataManager.ENDIMATIONS.get(this.instructionsLocation).processInstructions(model);
	}

	@Nullable
	public ResourceLocation getInstructionsLocation() {
		return this.instructionsLocation;
	}

	/**
	 * @return - The duration of this animation; measured in ticks.
	 */
	public int getAnimationTickDuration() {
		return this.tickDuration;
	}
}