package client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamabnormals.blueprint.core.endimator.Endimation;
import com.teamabnormals.blueprint.core.endimator.Endimator;
import common.block.entity.TestEndimatedBlockEntity;
import core.BlueprintTest;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public final class TestEndimatedBlockEntityRenderer implements BlockEntityRenderer<TestEndimatedBlockEntity> {
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(new ResourceLocation(BlueprintTest.MOD_ID, "textures/entity/endimated_statue.png"));
	private final Endimator endimator;
	private final ModelPart root;

	public TestEndimatedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.endimator = Endimator.shortCompile(this.root = createBodyLayer().bakeRoot());
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -0.5F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.5F, 0.0F));
		PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));
		PartDefinition rightLeg = body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition leftLeg = body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -8.0F, 0.0F));
		PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -11.0F, 0.0F));
		PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -11.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -16.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void render(TestEndimatedBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int p_112311_, int p_112312_) {
		Endimation endimation = entity.getPlayingEndimation().asEndimation();
		assert endimation != null;
		float time = (entity.getAnimationTick() + partialTicks) * 0.05F;
		float length = endimation.getLength();
		if (time > length) {
			time = length;
		}
		this.endimator.apply(endimation, time, Endimator.ResetMode.ALL);
		entity.getEffectHandler().update(endimation, time);
		poseStack.pushPose();
		poseStack.translate(0.5D, 1.5D, 0.5D);
		poseStack.scale(1.0F, -1.0F, -1.0F);
		this.root.render(poseStack, bufferSource.getBuffer(RENDER_TYPE), 240, p_112312_);
		poseStack.popPose();
	}
}
