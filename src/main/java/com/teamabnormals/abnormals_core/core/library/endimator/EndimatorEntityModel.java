package com.teamabnormals.abnormals_core.core.library.endimator;

import java.util.List;

import com.google.common.collect.Lists;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author - SmellyModder(Luke Tonon)
 * @param <E> - The Entity for the Model; Vanilla needs this by default so it will be used here as well
 */
@OnlyIn(Dist.CLIENT)
public abstract class EndimatorEntityModel<E extends Entity & IEndimatedEntity> extends EntityModel<E> {
	protected List<EndimatorModelRenderer> savedBoxes = Lists.newArrayList();
	private EndimatorModelRenderer scaleController;
	protected Endimator endimator = new Endimator();
	protected E entity;
	
	/**
	 * Animates the model, should be called in Model#render before rendering
	 * @param endimatedEntity - The entity to be animated; should be supplied with {@link EndimatorEntityModel#entity}
	 */
	public void animateModel(E endimatedEntity) {}
	
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
	
	/*
	 * Creates a scale controller
	 * A EndimatorModelRenderer with basic positioning that can be used to get its values to be used as a scaling method
	 */
	public void createScaleController() {
		this.scaleController = new EndimatorModelRenderer(this, 0, 0);
		this.scaleController.showModel = false;
		this.scaleController.setRotationPoint(1, 1, 1);
	}
	
	/*
	 * Gets the scale controller
	 */
	public EndimatorModelRenderer getScaleController() {
		return this.scaleController;
	}
}