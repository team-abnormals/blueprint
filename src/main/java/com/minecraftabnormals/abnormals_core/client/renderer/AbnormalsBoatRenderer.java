package com.minecraftabnormals.abnormals_core.client.renderer;

import com.minecraftabnormals.abnormals_core.common.entity.AbnormalsBoat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The {@link EntityRenderer} responsible for the rendering of Abnormals Core's boat entities.
 */
@OnlyIn(Dist.CLIENT)
public class AbnormalsBoatRenderer extends EntityRenderer<AbnormalsBoat> {
	private final BoatModel model;

	public AbnormalsBoatRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new BoatModel(BoatModel.createBodyModel().bakeRoot());
		this.shadowRadius = 0.8F;
	}

	@Override
	public void render(AbnormalsBoat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLightIn) {
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.375D, 0.0D);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
		float f = (float) entity.getHurtTime() - partialTicks;
		float f1 = entity.getDamage() - partialTicks;
		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f > 0.0F) {
			poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) entity.getHurtDir()));
		}

		float f2 = entity.getBubbleAngle(partialTicks);
		if (!Mth.equal(f2, 0.0F)) {
			poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), entity.getBubbleAngle(partialTicks), true));
		}

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		this.model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = source.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
		this.model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		VertexConsumer vertexConsumer1 = source.getBuffer(RenderType.waterMask());
		this.model.waterPatch().render(poseStack, vertexConsumer1, packedLightIn, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, source, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(AbnormalsBoat entity) {
		return entity.getBoat().getTexture();
	}
}