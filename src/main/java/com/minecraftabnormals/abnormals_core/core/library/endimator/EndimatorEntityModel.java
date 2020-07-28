package com.minecraftabnormals.abnormals_core.core.library.endimator;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minecraftabnormals.abnormals_core.client.ClientInfo;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.library.SimpleTransform;
import com.minecraftabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author - SmellyModder(Luke Tonon)
 * @param <E> - The Entity for the Model
 */
@OnlyIn(Dist.CLIENT)
public abstract class EndimatorEntityModel<E extends Entity & IEndimatedEntity> extends EntityModel<E> {
	private Map<EndimatorModelRenderer, SimpleTransform> animationBoxValues = Maps.newHashMap();
	private Map<EndimatorModelRenderer, SimpleTransform> prevAnimationBoxValues = Maps.newHashMap();
	public List<EndimatorModelRenderer> savedBoxes = Lists.newArrayList();
	protected E entity;
	private int tickDuration;
	private int prevTickDuration;
	
	/**
	 * Animates the model, should be called in Model#render before rendering
	 * @param endimatedEntity - The entity to be animated; should be supplied with {@link EndimatorEntityModel#entity}
	 */
	public void animateModel(E endimatedEntity) {
		this.tickDuration = this.prevTickDuration = 0;
		this.animationBoxValues.clear();
		this.prevAnimationBoxValues.clear();
	}
	
	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.animateModel(this.entity);
	}
	
	@Override
	public void setRotationAngles(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.entity = entity;
		this.revertBoxesToDefaultValues();
	}
	
	/**
	 * Adds a EndimatorModelRenderer to the savedBoxes list to be used in {@link EndimatorEntityModel#setDefaultBoxValues} & {@link EndimatorEntityModel#revertBoxesToDefaultValues}
	 * @param box - The EndimatorModelRenderer to be added to the list
	 */
	public void addBoxToSavedBoxes(EndimatorModelRenderer box) {
		this.savedBoxes.add(box);
	}
	
	/*
	 * Sets the default box values, should be called after all EndimatorModelRenderers have been initialized on the EndimatorEntityModel
	 */
	public void setDefaultBoxValues() {
		this.savedBoxes.forEach((rendererModel) -> {
			if(rendererModel instanceof EndimatorModelRenderer) {
				((EndimatorModelRenderer) rendererModel).setDefaultBoxValues();
			}
		});
	}
	
	/*
	 * Should be called in EndimatorEntityModel#setRotationAngles; called by default
	 */
	public void revertBoxesToDefaultValues() {
		this.savedBoxes.forEach((rendererModel) -> {
			if(rendererModel instanceof EndimatorModelRenderer) {
				((EndimatorModelRenderer) rendererModel).revertToDefaultBoxValues();
			}
		});
	}
	
	/**
	 * Sets an Endimation to be played
	 * @param endimationToPlay - The animation to play
	 */
	public void setEndimationToPlay(Endimation endimationToPlay) {
		this.updateBoxValueMap();
		this.tickDuration = this.prevTickDuration = 0;
		if(this.entity.getPlayingEndimation() != endimationToPlay) {
			AbnormalsCore.LOGGER.warn("Endimation to be played doesn't match the Endimation playing on the entity!");
		}
	}
	
	/**
	 * Tries to play the Endimation specified
	 * @param endimationToPlay - The animation to play
	 * @return - Can the Endimation be played
	 */
	public boolean tryToPlayEndimation(Endimation endimationToPlay) {
		if(this.entity.isEndimationPlaying(endimationToPlay)) {
			this.setEndimationToPlay(endimationToPlay);
			return true;
		}
		return false;
	}
	
	/*
	 * Updates the animation box values
	 */
	private void updateBoxValueMap() {
		this.prevAnimationBoxValues.clear();
		this.prevAnimationBoxValues.putAll(this.animationBoxValues);
		this.animationBoxValues.clear();
	}
	
	/**
	 * Gets the values of a box stored from a map
	 * @param model - The EndimatorRendererModel to look up in the box values map
	 * @return - The EndimatorRendererModel's float array of box values from the box values map
	 */
	public SimpleTransform getBoxValues(EndimatorModelRenderer model) {
		return this.animationBoxValues.computeIfAbsent(model, (transform) -> SimpleTransform.copy(SimpleTransform.ZERO));
	}
	
	private boolean shouldApplyTransformToModelRenderers() {
		int animationTick = this.entity.getAnimationTick();
		return animationTick < this.tickDuration && animationTick >= this.prevTickDuration;
	}
	
	/**
	 * Starts a Keyframe for a set amount of ticks
	 * @param tickDuration - The duration of the keyframe; measured in ticks
	 */
	public void startKeyframe(int tickDuration) {
		this.prevTickDuration = this.tickDuration;
		this.tickDuration += tickDuration;
	}
	
	/**
	 * Starts a Keyframe that holds the most recent box values for a set duration
	 * @param tickDuration - The duration of the Keyframe; measured in ticks
	 */
	public void setStaticKeyframe(int tickDuration) {
		this.startKeyframe(tickDuration);
		if(this.shouldApplyTransformToModelRenderers()) {
			this.prevAnimationBoxValues.forEach(SimpleTransform.applyAdditiveTransformToEndimatorModelRenderer());
		}
	}
	
	/**
	 * Ends the current Keyframe
	 */
	public void endKeyframe() {
		if(this.shouldApplyTransformToModelRenderers()) {
			float intermediateTick = (this.entity.getAnimationTick() - this.prevTickDuration + ClientInfo.getPartialTicks()) / (this.tickDuration - this.prevTickDuration);
			float increment = MathHelper.sin((float) (intermediateTick * Math.PI / 2.0F));
			
			this.prevAnimationBoxValues.forEach(SimpleTransform.applyAdditiveTransformToEndimatorModelRendererWithMultiplier(1.0F - increment));
			this.animationBoxValues.forEach(SimpleTransform.applyAdditiveTransformToEndimatorModelRendererWithMultiplier(increment));
		}
		
		this.updateBoxValueMap();
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
	 * Rotates an EndimatorRendererModel in the current Keyframe
	 * @param model - EndimatorRendererModel to rotate
	 * @param x - The x rotation
	 * @param y - The y rotation
	 * @param z - The z rotation
	 */
	public void rotate(EndimatorModelRenderer model, float x, float y, float z) {
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
		this.getBoxValues(model).addRotation(x, y, z);
	}
	
	/**
	 * Moves an EndimatorRendererModel in the current Keyframe
	 * @param model - EndimatorRendererModel to move
	 * @param x - The x point
	 * @param y - The y point
	 * @param z - The z point
	 */
	public void move(EndimatorModelRenderer model, float x, float y, float z) {
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
		this.getBoxValues(model).addPosition(x, y, z);
	}
	
	/**
	 * Offsets an EndimatorRendererModel in the current Keyframe to the offsets specified
	 * @param model - EndimatorRendererModel to offset
	 * @param x - The x point
	 * @param y - The y point
	 * @param z - The z point
	 */
	public void offset(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).setOffset(x, y, z);
	}
	
	/**
	 * Offsets an EndimatorRendererModel in the current Keyframe to the current position plus the positions specified
	 * @param model - EndimatorRendererModel to offset
	 * @param x - The x point to add
	 * @param y - The y point to add
	 * @param z - The z point to add
	 */
	public void offsetAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).addOffset(x, y, z);
	}
	
	/**
	 * Scales an EndimatorRendererModel in the current Keyframe to the scale specified
	 * @param model - EndimatorRendererModel to scale
	 * @param x - The x scale
	 * @param y - The y scale
	 * @param z - The z scale
	 */
	public void scale(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).setScale(x, y, z);
	}
	
	/**
	 * Scales an EndimatorRendererModel in the current Keyframe to the current scale plus the scale specified
	 * @param model - EndimatorRendererModel to scale
	 * @param x - The x scale
	 * @param y - The y scale
	 * @param z - The z scale
	 */
	public void scaleAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).addScale(x, y, z);
	}
}