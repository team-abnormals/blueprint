package com.teamabnormals.blueprint.core.endimator.entity;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.Endimation;
import com.teamabnormals.blueprint.core.endimator.Endimator;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

/**
 * An {@link EntityModel} extension that simplifies the animation of {@link Endimatable} entities.
 *
 * @param <E> The type of entity for the model.
 * @author SmellyModder (Luke Tonon)
 */
@OnlyIn(Dist.CLIENT)
public abstract class EndimatorEntityModel<E extends Entity & Endimatable> extends EntityModel<E> {
	protected Endimator endimator = new Endimator(new HashMap<>());
	protected E entity;

	public EndimatorEntityModel() {
		super();
	}

	/**
	 * Updates this model's {@link #endimator}.
	 *
	 * @param endimatedEntity The entity to animate for.
	 */
	public void animateModel(E endimatedEntity, float partialTicks) {
		this.endimator.getPoseMap().forEach((partName, posedPart) -> {
			posedPart.reset();
			posedPart.part.reset();
		});
		PlayableEndimation playingEndimation = endimatedEntity.getPlayingEndimation();
		Endimation endimation = playingEndimation.asEndimation();
		if (endimation != null) {
			float time = (endimatedEntity.getAnimationTick() + partialTicks) * 0.05F;
			float length = endimation.getLength();
			if (time > length) {
				time = length;
			}
			this.endimator.apply(endimation, time, Endimator.ResetMode.NONE);
			endimatedEntity.getEffectHandler().update(endimation, time);
		}
	}

	@Override
	public void setupAnim(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.entity = entity;
		this.animateModel(entity, ClientInfo.getPartialTicks());
	}
}