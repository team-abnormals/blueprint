package com.minecraftabnormals.abnormals_core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

public class SlabfishHatModel extends Model {
	public final ModelPart body;
	public final ModelPart backpack;

	public SlabfishHatModel() {
		super(RenderType::entityCutoutNoCull);
		this.texWidth = 32;
		this.texHeight = 32;

		this.body = new ModelPart(this);
		this.body.setPos(0.0F, 0.0F, 0.0F);
		this.body.texOffs(0, 0).addBox(-5.0F, -18.0F, -4.0F, 10.0F, 10.0F, 4.0F, 0.0F, false);

		this.backpack = new ModelPart(this);
		this.backpack.setPos(0.0F, 0.0F, 0.0F);
		this.backpack.texOffs(8, 20).addBox(-4.0F, -16.0F, 0.0F, 8.0F, 8.0F, 4.0F, 0.0F, false);

		ModelPart leftArm = new ModelPart(this);
		leftArm.setPos(5.0F, -12.0F, -2.0F);
		this.body.addChild(leftArm);
		setRotationAngle(leftArm, 0.0F, 0.0F, -0.4363F);
		leftArm.texOffs(16, 14).addBox(0.0F, 0.0F, -1.5F, 1.0F, 3.0F, 3.0F, 0.0F, true);

		ModelPart rightLeg = new ModelPart(this);
		rightLeg.setPos(-2.5F, -8.0F, -1.0F);
		this.body.addChild(rightLeg);
		setRotationAngle(rightLeg, -1.3963F, 0.0F, 0.0F);
		rightLeg.texOffs(0, 14).addBox(-1.5F, -0.0868F, -3.4924F, 3.0F, 5.0F, 3.0F, 0.0F, false);

		ModelPart rightArm = new ModelPart(this);
		rightArm.setPos(-5.0F, -12.0F, -2.0F);
		this.body.addChild(rightArm);
		setRotationAngle(rightArm, 0.0F, 0.0F, 0.4363F);
		rightArm.texOffs(16, 14).addBox(-1.0F, 0.0F, -1.5F, 1.0F, 3.0F, 3.0F, 0.0F, false);

		ModelPart fin = new ModelPart(this);
		fin.setPos(0.0F, -12.0F, 0.0F);
		this.body.addChild(fin);
		setRotationAngle(fin, -0.2182F, 0.0F, 0.0F);
		fin.texOffs(24, 12).addBox(0.0F, -1.0F, 0.0F, 0.0F, 4.0F, 4.0F, 0.0F, false);

		ModelPart leftLeg = new ModelPart(this);
		leftLeg.setPos(2.5F, -8.0F, -1.0F);
		this.body.addChild(leftLeg);
		setRotationAngle(leftLeg, -1.3963F, 0.0F, 0.0F);
		leftLeg.texOffs(0, 14).addBox(-1.5F, -0.0868F, -3.4924F, 3.0F, 5.0F, 3.0F, 0.0F, true);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}