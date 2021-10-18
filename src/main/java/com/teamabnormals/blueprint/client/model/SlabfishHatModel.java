package com.teamabnormals.blueprint.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

/**
 * The {@link Model} for the slabfish patreon hat.
 * <p>For more information, visit the <a href="https://www.patreon.com/teamabnormals">Patreon</a>></p>
 */
public class SlabfishHatModel extends Model {
	public final ModelPart body;
	public final ModelPart backpack;

	public SlabfishHatModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.body = root.getChild("body");
		this.backpack = root.getChild("backpack");
	}

	public static LayerDefinition createBodyModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -18.0F, -4.0F, 10.0F, 10.0F, 4.0F, false), PartPose.offset(0.0F, 0.0F, 0.0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 0.0F, -1.5F, 1.0F, 3.0F, 3.0F, false), PartPose.offsetAndRotation(5.0F, -12.0F, -2.0F, 0.0F, 0.0F, -0.4363F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 14).addBox(-1.0F, 0.0F, -1.5F, 1.0F, 3.0F, 3.0F, false), PartPose.offsetAndRotation(-5.0F, -12.0F, -2.0F, 0.0F, 0.0F, 0.4363F));
		body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, -0.0868F, -3.4924F, 3.0F, 5.0F, 3.0F, false), PartPose.offsetAndRotation(2.5F, -8.0F, -1.0F, -1.3963F, 0.0F, 0.0F));
		body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, -0.0868F, -3.4924F, 3.0F, 5.0F, 3.0F, false), PartPose.offsetAndRotation(-2.5F, -8.0F, -1.0F, -1.3963F, 0.0F, 0.0F));
		body.addOrReplaceChild("fin", CubeListBuilder.create().texOffs(24, 12).addBox(0.0F, -1.0F, 0.0F, 0.0F, 4.0F, 4.0F, false), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("backpack", CubeListBuilder.create().texOffs(8, 20).addBox(-4.0F, -16.0F, 0.0F, 8.0F, 8.0F, 4.0F, false), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}