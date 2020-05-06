package com.teamabnormals.abnormals_core.core.library.endimator;

import java.util.Map;

import com.google.common.collect.Maps;
import com.teamabnormals.abnormals_core.client.ClientInfo;
import com.teamabnormals.abnormals_core.client.SimpleTransform;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Class used for animating an {@link IEndimatedEntity}
 * @author SmellyModder(Luke Tonon)
 */
@OnlyIn(Dist.CLIENT)
public class Endimator {
	private Map<EndimatorModelRenderer, SimpleTransform> boxValues;
	private Map<EndimatorModelRenderer, SimpleTransform> prevBoxValues;
	private IEndimatedEntity endimatedEntity;
	private int tickDuration;
	private int prevTickDuration;
	private boolean correctEndimation;
    
	public Endimator() {
		this.boxValues = Maps.newHashMap();
		this.prevBoxValues = Maps.newHashMap();
	}
	
	public boolean isCorrectEndimationBeingPlayed() {
		return this.correctEndimation;
	}
    
	/**
	 * Sets the animation for this endimator instance to play
	 * @param animationToPlay - The animation to play
	 * @return - Is this the correct animation to play
	 */
	public void setAnimationToPlay(Endimation animationToPlay) {
		this.updateBoxValueMap();
	    this.tickDuration = this.prevTickDuration = 0;
		this.correctEndimation = this.endimatedEntity.getPlayingEndimation() == animationToPlay;
	}
    
	/**
	 * Updates the entity for this endimator instance
	 * @param endimatedEntity - The entity to update
	 */
	public void setEntity(IEndimatedEntity endimatedEntity) {
		this.endimatedEntity = endimatedEntity;
	}
	
	/**
	 * Starts a Keyframe for a set amount of ticks
	 * @param tickDuration - The duration of the keyframe; measured in ticks
	 */
	public void startKeyframe(int tickDuration) {
		if(!this.isCorrectEndimationBeingPlayed()) return;
		
		this.prevTickDuration = this.tickDuration;
		this.tickDuration += tickDuration;
	}
    
	/**
	 * Ends the current Keyframe
	 */
	public void endKeyframe() {
		this.endKeyframe(false);
	}
	
	/**
	 * Starts a Keyframe that holds the most recent box values for a set duration
	 * @param tickDuration - The duration of the Keyframe; measured in ticks
	 */
	public void setStaticKeyframe(int tickDuration) {
		this.startKeyframe(tickDuration);
		this.endKeyframe(true);
	}
    
	/**
	 * Resets the current Keyframe to its default values
	 * @param tickDuration - The duration of the Keyframe; measured in ticks
	 */
	public void resetKeyframe(int tickDuration) {
		this.startKeyframe(tickDuration);
		this.endKeyframe();
	}
	
	/**
	 * Gets the values of a box stored from a map
	 * @param model - The EndimatorRendererModel to look up in the box values map
	 * @return - The EndimatorRendererModel's float array of box values from the box values map
	 */
	public SimpleTransform getBoxValues(EndimatorModelRenderer model) {
		return this.boxValues.computeIfAbsent(model, transform -> SimpleTransform.ZERO);
	}
	
	/**
	 * Moves an EndimatorRendererModel in the current Keyframe
	 * @param model - EndimatorRendererModel to move
	 * @param x - The x point
	 * @param y - The y point
	 * @param z - The z point
	 */
	public void move(EndimatorModelRenderer model, float x, float y, float z) {
		if(!this.isCorrectEndimationBeingPlayed()) return;
		this.getBoxValues(model).setPosition(x, y, z);
	}
	
	/**
	 * Moves an EndimatorRendererModel in the current Keyframe to the current position plus the positions specified
	 * @param model - EndimatorRendererModel to move
	 * @param x - The x point to add
	 * @param y - The y point to add
	 * @param z - The z point to add
	 */
	public void moveAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		if(!this.isCorrectEndimationBeingPlayed()) return;
		this.getBoxValues(model).addPosition(x, y, z);
	}
	
	/**
	 * Rotates an EndimatorRendererModel in the current Keyframe
	 * @param model - EndimatorRendererModel to rotate
	 * @param x - The x rotation
	 * @param y - The y rotation
	 * @param z - The z rotation
	 */
	public void rotate(EndimatorModelRenderer model, float x, float y, float z) {
		if(!this.isCorrectEndimationBeingPlayed()) return;
		this.getBoxValues(model).setRotation(x, y, z);
	}
	
	/**
	 * Rotates an EndimatorRendererModel in the current Keyframe to the current degrees plus the degrees specified
	 * @param model - EndimatorRendererModel to rotate
	 * @param x - The x rotation to add
	 * @param y - The y rotation to add
	 * @param z - The z rotation to add
	 */
	public void rotateAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		if(!this.isCorrectEndimationBeingPlayed()) return;
		this.getBoxValues(model).addRotation(x, y, z);
	}
	
	private void updateBoxValueMap() {
		this.prevBoxValues.clear();
		this.prevBoxValues.putAll(this.boxValues);
		this.boxValues.clear();
	}
	
	private void endKeyframe(boolean isStatic) {
		if(!this.isCorrectEndimationBeingPlayed()) return;
        
		int animationTick = this.endimatedEntity.getAnimationTick();
		
		if(animationTick >= this.prevTickDuration && animationTick < this.tickDuration) {
			if(isStatic) {
				this.prevBoxValues.forEach(SimpleTransform.transformAddModelRenderer());
			} else {
				float meanTick = (animationTick - this.prevTickDuration + ClientInfo.getPartialTicks()) / (this.tickDuration - this.prevTickDuration);
				float increment = MathHelper.sin((float) (meanTick * Math.PI / 2.0F));
				float decrement = 1.0F - increment;
				
				this.prevBoxValues.forEach(SimpleTransform.transformAddModelRendererWithMultiplier(decrement));
				this.boxValues.forEach(SimpleTransform.transformAddModelRendererWithMultiplier(increment));
			}
		}
		
		if(!isStatic) {
			this.updateBoxValueMap();
		}
	}
}