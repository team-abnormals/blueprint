package com.minecraftabnormals.abnormals_core.core.library;

import java.util.function.BiConsumer;

import com.minecraftabnormals.abnormals_core.core.library.endimator.EndimatorModelRenderer;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Class that can hold positions and rotations for 3D Space, has some other utility functions
 * Positions and Offsets are measured the same as Minecraft positioning; (1.0F = 1 Block)
 * Rotations are measured in radians; (x° × Math.PI / 180)
 * @author SmellyModder(Luke Tonon)
 */
public class SimpleTransform {
	public static final SimpleTransform ZERO = new SimpleTransform(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	private float posX, posY, posZ;
	private float offsetX, offsetY, offsetZ;
	private float angleX, angleY, angleZ;
	private float scaleX, scaleY, scaleZ;
	
	public SimpleTransform(float posX, float posY, float posZ, float angleX, float angleY, float angleZ) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;
	}
	
	public SimpleTransform(float posX, float posY, float posZ, float offsetX, float offsetY, float offsetZ, float angleX, float angleY, float angleZ) {
		this(posX, posY, posZ, angleX, angleY, angleZ);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
	}
	
	public SimpleTransform(float posX, float posY, float posZ, float offsetX, float offsetY, float offsetZ, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
		this(posX, posY, posZ, angleX, angleY, angleZ);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
	}
	
	public static SimpleTransform copy(SimpleTransform transform) {
		return new SimpleTransform(transform.posX, transform.posY, transform.posZ, transform.offsetY, transform.offsetY, transform.offsetZ, transform.angleX, transform.angleY, transform.angleZ, transform.scaleX, transform.scaleY, transform.scaleZ);
	}
	
	public void scale(Vector3d scale) {
		this.setPosition(this.posX * (float) scale.getX(), this.posX * (float) scale.getY(), this.posX * (float) scale.getZ());
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
	
	public void applyTransformToModelRenderer(ModelRenderer modelRenderer) {
		modelRenderer.rotationPointX = this.posX;
		modelRenderer.rotationPointY = this.posY;
		modelRenderer.rotationPointZ = this.posZ;
		modelRenderer.rotateAngleX = this.angleX;
		modelRenderer.rotateAngleY = this.angleY;
		modelRenderer.rotateAngleZ = this.angleZ;
	}
	
	public void applyTransformToEndimatorModelRenderer(EndimatorModelRenderer modelRenderer) {
		modelRenderer.rotationPointX = this.posX;
		modelRenderer.rotationPointY = this.posY;
		modelRenderer.rotationPointZ = this.posZ;
		modelRenderer.offsetX = this.offsetX;
		modelRenderer.offsetY = this.offsetY;
		modelRenderer.offsetZ = this.offsetZ;
		modelRenderer.rotateAngleX = this.angleX;
		modelRenderer.rotateAngleY = this.angleY;
		modelRenderer.rotateAngleZ = this.angleZ;
		modelRenderer.setScale(this.scaleX, this.scaleY, this.scaleZ);
	}
	
	public static BiConsumer<ModelRenderer, SimpleTransform> applyTransformToModelRenderer() {
		return (modelRenderer, transform) -> {
			transform.applyTransformToModelRenderer(modelRenderer);
		};
	}
	
	public static BiConsumer<EndimatorModelRenderer, SimpleTransform> applyTransformToEndimatorModelRenderer() {
		return (modelRenderer, transform) -> {
			transform.applyTransformToEndimatorModelRenderer(modelRenderer);
		};
	}
	
	public void applyAdditiveTransformToModelRenderer(ModelRenderer modelRenderer) {
		modelRenderer.rotationPointX += this.posX;
		modelRenderer.rotationPointY += this.posY;
		modelRenderer.rotationPointZ += this.posZ;
		modelRenderer.rotateAngleX += this.angleX;
		modelRenderer.rotateAngleY += this.angleY;
		modelRenderer.rotateAngleZ += this.angleZ;
	}
	
	public static BiConsumer<ModelRenderer, SimpleTransform> applyAdditiveTransformToModelRenderer() {
		return (modelRenderer, transform) -> {
			transform.applyAdditiveTransformToModelRenderer(modelRenderer);
		};
	}
	
	public void applyAdditiveTransformToEndimatorModelRenderer(EndimatorModelRenderer modelRenderer) {
		modelRenderer.rotationPointX += this.posX;
		modelRenderer.rotationPointY += this.posY;
		modelRenderer.rotationPointZ += this.posZ;
		modelRenderer.offsetX += this.offsetX;
		modelRenderer.offsetY += this.offsetY;
		modelRenderer.offsetZ += this.offsetZ;
		modelRenderer.rotateAngleX += this.angleX;
		modelRenderer.rotateAngleY += this.angleY;
		modelRenderer.rotateAngleZ += this.angleZ;
		modelRenderer.scaleX += this.scaleX;
		modelRenderer.scaleY += this.scaleY;
		modelRenderer.scaleZ += this.scaleZ;
	}
	
	public static BiConsumer<EndimatorModelRenderer, SimpleTransform> applyAdditiveTransformToEndimatorModelRenderer() {
		return (modelRenderer, transform) -> {
			transform.applyAdditiveTransformToEndimatorModelRenderer(modelRenderer);
		};
	}
	
	public void applyAdditiveTransformToModelRendererWithMultiplier(ModelRenderer modelRenderer, float multiplier) {
		modelRenderer.rotationPointX += multiplier * this.posX;
		modelRenderer.rotationPointY += multiplier * this.posY;
		modelRenderer.rotationPointZ += multiplier * this.posZ;
		modelRenderer.rotateAngleX += multiplier * this.angleX;
		modelRenderer.rotateAngleY += multiplier * this.angleY;
		modelRenderer.rotateAngleZ += multiplier * this.angleZ;
	}
	
	public static BiConsumer<ModelRenderer, SimpleTransform> applyAdditiveTransformToModelRendererWithMultiplier(float multiplier) {
		return (modelRenderer, transform) -> {
			transform.applyAdditiveTransformToModelRendererWithMultiplier(modelRenderer, multiplier);
		};
	}
	
	public void applyAdditiveTransformToEndimatorModelRendererWithMultiplier(EndimatorModelRenderer modelRenderer, float multiplier) {
		modelRenderer.rotationPointX += multiplier * this.posX;
		modelRenderer.rotationPointY += multiplier * this.posY;
		modelRenderer.rotationPointZ += multiplier * this.posZ;
		modelRenderer.offsetX += multiplier * this.offsetX;
		modelRenderer.offsetY += multiplier * this.offsetY;
		modelRenderer.offsetZ += multiplier * this.offsetZ;
		modelRenderer.rotateAngleX += multiplier * this.angleX;
		modelRenderer.rotateAngleY += multiplier * this.angleY;
		modelRenderer.rotateAngleZ += multiplier * this.angleZ;
		modelRenderer.scaleX += multiplier * this.scaleX;
		modelRenderer.scaleY += multiplier * this.scaleY;
		modelRenderer.scaleZ += multiplier * this.scaleZ;
	}
	
	public static BiConsumer<EndimatorModelRenderer, SimpleTransform> applyAdditiveTransformToEndimatorModelRendererWithMultiplier(float multiplier) {
		return (modelRenderer, transform) -> {
			transform.applyAdditiveTransformToEndimatorModelRendererWithMultiplier(modelRenderer, multiplier);
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
	
	public float getOffsetX() {
		return this.offsetX;
	}
	
	public float getOffsetY() {
		return this.offsetY;
	}
	
	public float getOffsetZ() {
		return this.offsetZ;
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
	
	public float getScaleX() {
		return this.scaleX;
	}
	
	public float getScaleY() {
		return this.scaleY;
	}
	
	public float getScaleZ() {
		return this.scaleZ;
	}
}