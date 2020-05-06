package com.teamabnormals.abnormals_core.client;

import java.util.function.BiConsumer;

import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * Class that can hold positions and rotations for 3D Space, has some other utility functions
 * @author SmellyModder(Luke Tonon)
 */
public class SimpleTransform {
	public static final SimpleTransform ZERO = new SimpleTransform(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	private float posX, posY, posZ;
	private float angleX, angleY, angleZ;
	
	public SimpleTransform(float posX, float posY, float posZ, float angleX, float angleY, float angleZ) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;
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
	
	public void transformModelRenderer(ModelRenderer modelRenderer) {
		modelRenderer.rotationPointX = this.posX;
		modelRenderer.rotationPointY = this.posY;
		modelRenderer.rotationPointZ = this.posZ;
		modelRenderer.rotateAngleX = this.angleX;
		modelRenderer.rotateAngleY = this.angleY;
		modelRenderer.rotateAngleZ = this.angleZ;
	}
	
	public static BiConsumer<ModelRenderer, SimpleTransform> transformModelRenderer() {
		return (modelRenderer, transform) -> {
			modelRenderer.rotationPointX = transform.posX;
			modelRenderer.rotationPointY = transform.posY;
			modelRenderer.rotationPointZ = transform.posZ;
			modelRenderer.rotateAngleX = transform.angleX;
			modelRenderer.rotateAngleY = transform.angleY;
			modelRenderer.rotateAngleZ = transform.angleZ;
		};
	}
	
	public void transformAddModelRenderer(ModelRenderer modelRenderer) {
		modelRenderer.rotationPointX += this.posX;
		modelRenderer.rotationPointY += this.posY;
		modelRenderer.rotationPointZ += this.posZ;
		modelRenderer.rotateAngleX += this.angleX;
		modelRenderer.rotateAngleY += this.angleY;
		modelRenderer.rotateAngleZ += this.angleZ;
	}
	
	public static BiConsumer<ModelRenderer, SimpleTransform> transformAddModelRenderer() {
		return (modelRenderer, transform) -> {
			modelRenderer.rotationPointX += transform.posX;
			modelRenderer.rotationPointY += transform.posY;
			modelRenderer.rotationPointZ += transform.posZ;
			modelRenderer.rotateAngleX += transform.angleX;
			modelRenderer.rotateAngleY += transform.angleY;
			modelRenderer.rotateAngleZ += transform.angleZ;
		};
	}
	
	public void transformAddModelRendererWithMultiplier(ModelRenderer modelRenderer, float multiplier) {
		modelRenderer.rotationPointX += multiplier * this.posX;
		modelRenderer.rotationPointY += multiplier * this.posY;
		modelRenderer.rotationPointZ += multiplier * this.posZ;
		modelRenderer.rotateAngleX += multiplier * this.angleX;
		modelRenderer.rotateAngleY += multiplier * this.angleY;
		modelRenderer.rotateAngleZ += multiplier * this.angleZ;
	}
	
	public static BiConsumer<ModelRenderer, SimpleTransform> transformAddModelRendererWithMultiplier(float multiplier) {
		return (modelRenderer, transform) -> {
			modelRenderer.rotationPointX += multiplier * transform.posX;
			modelRenderer.rotationPointY += multiplier * transform.posY;
			modelRenderer.rotationPointZ += multiplier * transform.posZ;
			modelRenderer.rotateAngleX += multiplier * transform.angleX;
			modelRenderer.rotateAngleY += multiplier * transform.angleY;
			modelRenderer.rotateAngleZ += multiplier * transform.angleZ;
		};
	}
	
	public float getPosX() {
		return this.posX;
	}
	
	public float getPosY() {
		return this.posY;
	}
	
	public float getPosZ() {
		return this.posZ;
	}
	
	public float getAngleX() {
		return this.angleX;
	}
	
	public float getAngleY() {
		return this.angleY;
	}
	
	public float getAngleZ() {
		return this.angleZ;
	}
}