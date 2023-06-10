package com.teamabnormals.blueprint.core.endimator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamabnormals.blueprint.core.endimator.model.EndimatorPartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Map;

/**
 * An extension of vanilla's {@link ModelPart} class that allows for scaling and relative offsetting of parts.
 *
 * @author SmellyModder (Luke Tonon)
 */
@OnlyIn(Dist.CLIENT)
public class EndimatorModelPart extends ModelPart implements EndimatablePart {
	public float xOffset, yOffset, zOffset;
	public boolean scaleChildren = true;
	public EndimatorPartPose initialPartPose = new EndimatorPartPose();

	public EndimatorModelPart(List<Cube> cubes, Map<String, ModelPart> children) {
		super(cubes, children);
	}

	public EndimatorModelPart(ModelPart part) {
		this(part.cubes, part.children);
		this.copyFrom(part);
		this.setInitialPose(new EndimatorPartPose().setPartPose(part.getInitialPose()));
	}

	public EndimatorModelPart(ModelPart part, float xOffset, float yOffset, float zOffset, float xScale, float yScale, float zScale, boolean scaleChildren) {
		this(part.cubes, part.children);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.xScale = xScale;
		this.yScale = yScale;
		this.zScale = zScale;
		this.scaleChildren = scaleChildren;
	}

	public EndimatorModelPart(ModelPart part, float xOffset, float yOffset, float zOffset) {
		this(part.cubes, part.children);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}

	public EndimatorModelPart(ModelPart part, float xScale, float yScale, float zScale, boolean scaleChildren) {
		this(part.cubes, part.children);
		this.xScale = xScale;
		this.yScale = yScale;
		this.zScale = zScale;
		this.scaleChildren = scaleChildren;
	}

	/**
	 * Sets the scale dimensions of this part.
	 *
	 * @param x The new x scale.
	 * @param y The new y scale.
	 * @param z The new z scale.
	 */
	public void setScale(float x, float y, float z) {
		this.xScale = x;
		this.yScale = y;
		this.zScale = z;
	}

	/**
	 * Sets if this part should scale its children with its own scale.
	 *
	 * @param scaleChildren If this part should scale its children with its own scale.
	 */
	public void setShouldScaleChildren(boolean scaleChildren) {
		this.scaleChildren = scaleChildren;
	}

	/**
	 * Sets the dimensional offsets of this part.
	 *
	 * @param x The new x offset.
	 * @param y The new y offset.
	 * @param z The new z offset.
	 */
	public void setOffset(float x, float y, float z) {
		this.xOffset = x;
		this.yOffset = y;
		this.zOffset = z;
	}

	@Override
	public void resetPose() {
		this.loadPose(this.initialPartPose);
	}

	@Override
	public void reset() {
		this.resetPose();
	}

	@Override
	public void render(PoseStack pose, VertexConsumer consumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.visible) {
			Map<String, ModelPart> children = this.children;
			if (!this.cubes.isEmpty() || !children.isEmpty()) {
				pose.pushPose();

				pose.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
				if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
					pose.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
				}

				if (this.scaleChildren) {
					pose.translate(this.xOffset, this.yOffset, this.zOffset);
					pose.scale(this.xScale, this.yScale, this.zScale);
					this.compile(pose.last(), consumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				} else {
					pose.pushPose();
					pose.translate(this.xOffset, this.yOffset, this.zOffset);
					pose.scale(this.xScale, this.yScale, this.zScale);
					this.compile(pose.last(), consumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
					pose.popPose();
				}
				for (ModelPart part : children.values()) {
					part.render(pose, consumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				}
				pose.popPose();
			}
		}
	}

	private void compile(PoseStack.Pose pose, VertexConsumer consumer, int p_104293_, int p_104294_, float p_104295_, float p_104296_, float p_104297_, float p_104298_) {
		for (Cube cube : this.cubes) {
			cube.compile(pose, consumer, p_104293_, p_104294_, p_104295_, p_104296_, p_104297_, p_104298_);
		}
	}

	@Override
	public void addOffset(float x, float y, float z) {
		this.xOffset -= x;
		this.yOffset -= y;
		this.zOffset -= z;
	}

	/**
	 * Loads the pose values of an {@link EndimatorPartPose} instance onto this part.
	 *
	 * @param endimatorPartPose An {@link EndimatorPartPose} instance to load its pose values onto this part.
	 */
	public void loadPose(EndimatorPartPose endimatorPartPose) {
		this.loadPose(endimatorPartPose.partPose);
		this.setOffset(endimatorPartPose.offsetX, endimatorPartPose.offsetY, endimatorPartPose.offsetZ);
		this.setScale(endimatorPartPose.scaleX, endimatorPartPose.scaleY, endimatorPartPose.scaleZ);
	}

	/**
	 * Sets the initial pose values.
	 *
	 * @param endimatorPartPose An {@link EndimatorPartPose} instance to set the initial pose values.
	 */
	public void setInitialPose(EndimatorPartPose endimatorPartPose) {
		this.setInitialPose(endimatorPartPose.partPose);
		this.initialPartPose = endimatorPartPose;
	}
}