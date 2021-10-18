package com.teamabnormals.blueprint.core.endimator;

import com.google.common.collect.Maps;
import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.endimator.entity.EndimatorModelRenderer;
import com.teamabnormals.blueprint.core.endimator.entity.IEndimatedEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;

import java.util.Map;

/**
 * This is the core class for processing an {@link Endimation} for an {@link IEndimatedEntity}.
 * TODO: Possibly add support for more values to be stored in {@link BoxValues} so complex/unique custom instructions can be added.
 *
 * @param <E> The type of the entity this {@link Endimator} processes animations for.
 * @author SmellyModder (Luke Tonon)
 */
public final class Endimator<E extends Entity & IEndimatedEntity> {
	private final Map<EndimatorModelRenderer, BoxValues> prevBoxValues = Maps.newHashMap();
	private final Map<EndimatorModelRenderer, BoxValues> boxValues = Maps.newHashMap();
	private E entity;
	private int prevTickDuration;
	private int tickDuration;

	/**
	 * Updates this {@link Endimator} for an entity.
	 * <p>This should be called before performing any animating functions.</p>
	 *
	 * @param entity The entity to update this for.
	 */
	public void tick(E entity) {
		this.entity = entity;
		this.tickDuration = this.prevTickDuration = 0;
		this.prevBoxValues.clear();
		this.boxValues.clear();
	}

	/**
	 * Sets an Endimation to be played.
	 *
	 * @param endimationToPlay The {@link Endimation} to start playing.
	 */
	public void setEndimationToPlay(Endimation endimationToPlay) {
		this.updateBoxValueMap();
		this.tickDuration = this.prevTickDuration = 0;
		if (this.entity.getPlayingEndimation() != endimationToPlay) {
			Blueprint.LOGGER.warn("Endimation to be played doesn't match the Endimation playing on the entity!");
		}
	}

	/**
	 * Tries to begin playing an {@link Endimation}.
	 *
	 * @param endimationToPlay The {@link Endimation} to try to play.
	 * @return True if the {@link Endimation} was able to be played.
	 */
	public boolean tryToPlayEndimation(Endimation endimationToPlay) {
		if (this.entity.isEndimationPlaying(endimationToPlay)) {
			this.setEndimationToPlay(endimationToPlay);
			return true;
		}
		return false;
	}

	/**
	 * Starts a keyframe for a set amount of ticks.
	 *
	 * @param tickDuration The duration of the keyframe (measured in ticks).
	 */
	public void startKeyframe(int tickDuration) {
		this.prevTickDuration = this.tickDuration;
		this.tickDuration += tickDuration;
	}

	/**
	 * Starts a keyframe that holds the most recent box values for a set duration.
	 *
	 * @param tickDuration The duration of the keyframe (measured in ticks).
	 */
	public void setStaticKeyframe(int tickDuration) {
		this.startKeyframe(tickDuration);
		if (this.shouldEndimateBoxes()) {
			this.prevBoxValues.forEach((endimatorModelRenderer, values) -> values.addValuesToBox(endimatorModelRenderer));
		}
	}

	/**
	 * Ends the current Keyframe
	 */
	public void endKeyframe() {
		if (this.shouldEndimateBoxes()) {
			float increment = Mth.sin((float) (((this.entity.getAnimationTick() - this.prevTickDuration + ClientInfo.getPartialTicks()) / (this.tickDuration - this.prevTickDuration)) * Math.PI / 2.0F));
			this.prevBoxValues.forEach((endimatorModelRenderer, prevValues) -> prevValues.addValuesToBoxWithMultiplier(endimatorModelRenderer, 1.0F - increment));
			this.boxValues.forEach((endimatorModelRenderer, values) -> values.addValuesToBoxWithMultiplier(endimatorModelRenderer, increment));
		}

		this.updateBoxValueMap();
	}

	/**
	 * Resets the current keyframe to its default values.
	 *
	 * @param tickDuration The duration of the keyframe (measured in ticks).
	 */
	public void resetKeyframe(int tickDuration) {
		this.startKeyframe(tickDuration);
		this.endKeyframe();
	}

	/**
	 * Moves an {@link EndimatorModelRenderer} in the current keyframe to a position.
	 *
	 * @param model The {@link EndimatorModelRenderer} to move.
	 * @param x     The x point.
	 * @param y     The y point.
	 * @param z     The z point.
	 */
	public void move(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).setPosition(x, y, z);
	}

	/**
	 * Moves an {@link EndimatorModelRenderer} in the current keyframe to a position plus the current position.
	 *
	 * @param model The {@link EndimatorModelRenderer} to move.
	 * @param x     The x point to add.
	 * @param y     The y point to add.
	 * @param z     The z point to add.
	 */
	public void moveAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).addPosition(x, y, z);
	}

	/**
	 * Offsets an {@link EndimatorModelRenderer} in the current keyframe by a set of values.
	 *
	 * @param model The {@link EndimatorModelRenderer} to offset.
	 * @param x     The x point.
	 * @param y     The y point.
	 * @param z     The z point.
	 */
	public void offset(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).setOffset(x, y, z);
	}

	/**
	 * Offsets an {@link EndimatorModelRenderer} in the current keyframe by a set of values plus the current offset.
	 *
	 * @param model The {@link EndimatorModelRenderer} to offset.
	 * @param x     The x point to add.
	 * @param y     The y point to add.
	 * @param z     The z point to add.
	 */
	public void offsetAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).addOffset(x, y, z);
	}

	/**
	 * Rotates an {@link EndimatorModelRenderer} in the current keyframe to a set of rotations (measured in radians).
	 *
	 * @param model The {@link EndimatorModelRenderer} to rotate.
	 * @param x     The x rotation.
	 * @param y     The y rotation.
	 * @param z     The z rotation.
	 */
	public void rotate(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).setRotation(x, y, z);
	}

	/**
	 * Rotates an {@link EndimatorModelRenderer} in the current keyframe to a set of rotations (measured in radians) plus the current rotations.
	 *
	 * @param model The {@link EndimatorModelRenderer} to rotate.
	 * @param x     The x rotation to add.
	 * @param y     The y rotation to add.
	 * @param z     The z rotation to add.
	 */
	public void rotateAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).addRotation(x, y, z);
	}

	/**
	 * Scales an {@link EndimatorModelRenderer} in the current keyframe to the scale specified.
	 *
	 * @param model {@link EndimatorModelRenderer} to scale.
	 * @param x     The x scale.
	 * @param y     The y scale.
	 * @param z     The z scale.
	 */
	public void scale(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).setScale(x, y, z);
	}

	/**
	 * Scales an {@link EndimatorModelRenderer} in the current keyframe to the scale specified plus its current scale.
	 *
	 * @param model {@link EndimatorModelRenderer} to scale.
	 * @param x     The x scale to add.
	 * @param y     The y scale to add.
	 * @param z     The z scale to add.
	 */
	public void scaleAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.getBoxValues(model).addScale(x, y, z);
	}

	private boolean shouldEndimateBoxes() {
		int animationTick = this.entity.getAnimationTick();
		return animationTick < this.tickDuration && animationTick >= this.prevTickDuration;
	}

	/**
	 * Gets the current {@link BoxValues} for the specified {@link EndimatorModelRenderer}.
	 *
	 * @param model The {@link EndimatorModelRenderer} to get the values for.
	 * @return The {@link BoxValues} for the specified {@link EndimatorModelRenderer}.
	 */
	public BoxValues getBoxValues(EndimatorModelRenderer model) {
		return this.boxValues.computeIfAbsent(model, (modelRenderer) -> new BoxValues());
	}

	private void updateBoxValueMap() {
		this.prevBoxValues.clear();
		this.prevBoxValues.putAll(this.boxValues);
		this.boxValues.clear();
	}

	public static class BoxValues {
		private float posX, posY, posZ;
		private float offsetX, offsetY, offsetZ;
		private float angleX, angleY, angleZ;
		private float scaleX, scaleY, scaleZ;

		public BoxValues(float posX, float posY, float posZ, float offsetX, float offsetY, float offsetZ, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.angleX = angleX;
			this.angleY = angleY;
			this.angleZ = angleZ;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.scaleZ = scaleZ;
		}

		public BoxValues() {
			this(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		}

		public void setPosition(float posX, float posY, float posZ) {
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
		}

		public void addPosition(float posX, float posY, float posZ) {
			this.posX += posX;
			this.posY += posY;
			this.posZ += posZ;
		}

		public void setOffset(float offsetX, float offsetY, float offsetZ) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
		}

		public void addOffset(float offsetX, float offsetY, float offsetZ) {
			this.offsetX += offsetX;
			this.offsetY += offsetY;
			this.offsetZ += offsetZ;
		}

		public void setRotation(float angleX, float angleY, float angleZ) {
			this.angleX = angleX;
			this.angleY = angleY;
			this.angleZ = angleZ;
		}

		public void addRotation(float angleX, float angleY, float angleZ) {
			this.angleX += angleX;
			this.angleY += angleY;
			this.angleZ += angleZ;
		}

		public void setScale(float scaleX, float scaleY, float scaleZ) {
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.scaleZ = scaleZ;
		}

		public void addScale(float scaleX, float scaleY, float scaleZ) {
			this.scaleX += scaleX;
			this.scaleY += scaleY;
			this.scaleZ += scaleZ;
		}

		public void addValuesToBox(EndimatorModelRenderer modelRenderer) {
			modelRenderer.x += this.posX;
			modelRenderer.y += this.posY;
			modelRenderer.z += this.posZ;
			modelRenderer.offsetX += this.offsetX;
			modelRenderer.offsetY += this.offsetY;
			modelRenderer.offsetZ += this.offsetZ;
			modelRenderer.xRot += this.angleX;
			modelRenderer.yRot += this.angleY;
			modelRenderer.zRot += this.angleZ;
			modelRenderer.scaleX += this.scaleX;
			modelRenderer.scaleY += this.scaleY;
			modelRenderer.scaleZ += this.scaleZ;
		}

		public void addValuesToBoxWithMultiplier(EndimatorModelRenderer modelRenderer, float multiplier) {
			modelRenderer.x += multiplier * this.posX;
			modelRenderer.y += multiplier * this.posY;
			modelRenderer.z += multiplier * this.posZ;
			modelRenderer.offsetX += multiplier * this.offsetX;
			modelRenderer.offsetY += multiplier * this.offsetY;
			modelRenderer.offsetZ += multiplier * this.offsetZ;
			modelRenderer.xRot += multiplier * this.angleX;
			modelRenderer.yRot += multiplier * this.angleY;
			modelRenderer.zRot += multiplier * this.angleZ;
			modelRenderer.scaleX += multiplier * this.scaleX;
			modelRenderer.scaleY += multiplier * this.scaleY;
			modelRenderer.scaleZ += multiplier * this.scaleZ;
		}
	}
}
