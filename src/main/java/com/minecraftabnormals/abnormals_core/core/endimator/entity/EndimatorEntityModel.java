package com.minecraftabnormals.abnormals_core.core.endimator.entity;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.minecraftabnormals.abnormals_core.core.endimator.Endimator;
import com.minecraftabnormals.abnormals_core.core.endimator.Endimation;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @param <E> The Entity for the Model
 * @author SmellyModder(Luke Tonon)
 */
@OnlyIn(Dist.CLIENT)
public abstract class EndimatorEntityModel<E extends Entity & IEndimatedEntity> extends EntityModel<E> {
	public List<EndimatorModelRenderer> savedBoxes = Lists.newArrayList();
	protected final Endimator<E> endimator = new Endimator<>();
	protected E entity;

	/**
	 * Updates this model's {@link Endimator}.
	 *
	 * @param endimatedEntity The entity to be animated, should be supplied with {@link EndimatorEntityModel#entity}
	 */
	public void animateModel(E endimatedEntity) {
		this.endimator.tick(endimatedEntity);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.animateModel(this.entity);
	}

	@Override
	public void setupAnim(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.entity = entity;
		this.revertBoxesToDefaultValues();
	}

	/**
	 * Adds a {@link EndimatorModelRenderer} to the {@link #savedBoxes} list to be used in {@link EndimatorEntityModel#setDefaultBoxValues} & {@link EndimatorEntityModel#revertBoxesToDefaultValues}.
	 *
	 * @param box The {@link EndimatorModelRenderer} to be added to the list.
	 */
	public void addBoxToSavedBoxes(EndimatorModelRenderer box) {
		this.savedBoxes.add(box);
	}

	/**
	 * Sets the default box values for all {@link EndimatorModelRenderer}s in {@link #savedBoxes}.
	 * <p>This should be called in the constructor of the model.</p>
	 */
	public void setDefaultBoxValues() {
		this.savedBoxes.forEach(EndimatorModelRenderer::setDefaultBoxValues);
	}

	/**
	 * Resets all the {@link EndimatorModelRenderer}s in {@link #savedBoxes} to their default values.
	 * <p>This is called in {@link #setRotationAngles(Entity, float, float, float, float, float)}.</p>
	 */
	public void revertBoxesToDefaultValues() {
		this.savedBoxes.forEach(EndimatorModelRenderer::revertToDefaultBoxValues);
	}

	/**
	 * Sets an Endimation to be played.
	 *
	 * @param endimationToPlay The {@link Endimation} to start playing.
	 */
	public void setEndimationToPlay(Endimation endimationToPlay) {
		this.endimator.setEndimationToPlay(endimationToPlay);
	}

	/**
	 * Tries to begin playing an {@link Endimation}.
	 *
	 * @param endimationToPlay The {@link Endimation} to try to play.
	 * @return True if the {@link Endimation} was able to be played.
	 */
	public boolean tryToPlayEndimation(Endimation endimationToPlay) {
		return this.endimator.tryToPlayEndimation(endimationToPlay);
	}

	/**
	 * Starts a keyframe for a set amount of ticks.
	 *
	 * @param tickDuration The duration of the keyframe (measured in ticks).
	 */
	public void startKeyframe(int tickDuration) {
		this.endimator.startKeyframe(tickDuration);
	}

	/**
	 * Starts a keyframe that holds the most recent box values for a set duration.
	 *
	 * @param tickDuration The duration of the keyframe (measured in ticks).
	 */
	public void setStaticKeyframe(int tickDuration) {
		this.endimator.setStaticKeyframe(tickDuration);
	}

	/**
	 * Ends the current Keyframe
	 */
	public void endKeyframe() {
		this.endimator.endKeyframe();
	}

	/**
	 * Resets the current keyframe to its default values.
	 *
	 * @param tickDuration The duration of the keyframe (measured in ticks).
	 */
	public void resetKeyframe(int tickDuration) {
		this.endimator.resetKeyframe(tickDuration);
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
		this.endimator.move(model, x, y, z);
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
		this.endimator.moveAdditive(model, x, y, z);
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
		this.endimator.offset(model, x, y, z);
	}

	/**
	 * Offsets an {@link EndimatorModelRenderer} in the current keyframe by a set of values plus the current offset.
	 *
	 * @param model The {@link EndimatorModelRenderer} to offset.
	 * @param x     The x point.
	 * @param y     The y point.
	 * @param z     The z point.
	 */
	public void offsetAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.endimator.offsetAdditive(model, x, y, z);
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
		this.endimator.rotate(model, x, y, z);
	}

	/**
	 * Rotates an {@link EndimatorModelRenderer} in the current keyframe to a set of rotations (measured in radians) plus the current rotations.
	 *
	 * @param model The {@link EndimatorModelRenderer} to rotate.
	 * @param x     The x rotation.
	 * @param y     The y rotation.
	 * @param z     The z rotation.
	 */
	public void rotateAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.endimator.rotateAdditive(model, x, y, z);
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
		this.endimator.scale(model, x, y, z);
	}

	/**
	 * Scales an {@link EndimatorModelRenderer} in the current keyframe to the scale specified plus its current scale.
	 *
	 * @param model {@link EndimatorModelRenderer} to scale.
	 * @param x     The x scale.
	 * @param y     The y scale.
	 * @param z     The z scale.
	 */
	public void scaleAdditive(EndimatorModelRenderer model, float x, float y, float z) {
		this.endimator.scaleAdditive(model, x, y, z);
	}
}