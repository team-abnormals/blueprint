package com.teamabnormals.abnormals_core.core.library.endimator;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager.EndimationConversion;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager.EndimationInstruction;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager.EndimationInstruction.ModelRendererEndimationInstruction;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager.InstructionType;
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
	 * Data Driven constructor for an Endimation
	 * @param tickDuration - Duration of how long the animation plays for; measured in ticks
	 * @param instructionsLocation - The location of the JSON instructions for this animation
	 */
	public Endimation(ResourceLocation instructionsLocation, int tickDuration) {
		this.instructionsLocation = instructionsLocation;
		this.tickDuration = tickDuration;
	}
	
	/**
	 * Simple constructor for an Endimation
	 * @param tickDuration - Duration of how long the animation plays for; measured in ticks
	 */
	public Endimation(int tickDuration) {
		this(null, tickDuration);
	}
	
	/**
	 * Empty constructor; used for making Endimations that do nothing
	 */
	public Endimation() {
		this(0);
	}
	
	/**
	 * Processes the Instructions for this animation from a JSON file
	 * @param modelRenderer - The model to process the animation for
	 */
	public <E extends Entity & IEndimatedEntity> void processInstructions(EndimatorEntityModel<E> model) {
		Objects.requireNonNull(this.instructionsLocation, () -> "Instructions are null, this should not be the case!");
		EndimationConversion conversion = EndimationDataManager.ENDIMATIONS.get(this.instructionsLocation);
		for(EndimationInstruction instructions : conversion.getInstructions()) {
			InstructionType type = instructions.type;
			int tickLength = instructions.tickLength;
			
			if(type == InstructionType.START_KEYFRAME) {
				model.startKeyframe(tickLength);
			} else if(type == InstructionType.END_KEYFRAME) {
				model.endKeyframe();
			} else if(type == InstructionType.STATIC_KEYFRAME) {
				model.setStaticKeyframe(tickLength);
			} else if(type == InstructionType.RESET_KEYFRAME) {
				model.resetKeyframe(tickLength);
			} else {
				ModelRendererEndimationInstruction instruction = (ModelRendererEndimationInstruction) instructions;
				EndimatorModelRenderer modelRenderer = getModelRendererByName(model, instruction.modelRenderer);
				float[] values = new float[] {instruction.x, instruction.y, instruction.z};
				if(type == InstructionType.MOVE) {
					model.move(modelRenderer, values[0], values[1], values[2]);
				} else if(type == InstructionType.ADD_MOVE) {
					model.moveAdditive(modelRenderer, values[0], values[1], values[2]);
				} else if(type == InstructionType.ROTATE) {
					model.rotate(modelRenderer, (float) Math.toRadians(values[0]), (float) Math.toRadians(values[1]), (float) Math.toRadians(values[2]));
				} else if(type == InstructionType.ADD_ROTATE) {
					model.rotateAdditive(modelRenderer, (float) Math.toRadians(values[0]), (float) Math.toRadians(values[1]), (float) Math.toRadians(values[2]));
				} else if(type == InstructionType.OFFSET) {
					model.offset(modelRenderer, values[0], values[1], values[2]);
				} else if(type == InstructionType.ADD_OFFSET) {
					model.offsetAdditive(modelRenderer, values[0], values[1], values[2]);
				}
			}
		}
	}
	
	private static EndimatorModelRenderer getModelRendererByName(EndimatorEntityModel<?> model, String name) {
		List<EndimatorModelRenderer> boxes = model.savedBoxes;
		boxes.removeIf((box) -> !(box.getName() != null && box.getName().equals(name)));
		return boxes.get(0);
	}
	
	/**
	 * @return - The duration of this animation; measured in ticks
	 */
	public int getAnimationTickDuration() {
		return this.tickDuration;
	}
}