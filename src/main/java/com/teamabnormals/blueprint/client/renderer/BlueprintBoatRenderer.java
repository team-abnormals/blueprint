package com.teamabnormals.blueprint.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The {@link EntityRenderer} responsible for the rendering of Blueprint's boat entities.
 */
@OnlyIn(Dist.CLIENT)
public class BlueprintBoatRenderer<E extends Boat & IBlueprintBoat> extends EntityRenderer<E> {
	private final BoatModel model;

	private BlueprintBoatRenderer(EntityRendererProvider.Context context, BoatModel model) {
		super(context);
		this.model = model;
		this.shadowRadius = 0.8F;
	}

	@Nonnull
	public static <E extends Boat & IBlueprintBoat> BlueprintBoatRenderer<E> simple(EntityRendererProvider.Context context) {
		return new BlueprintBoatRenderer<>(context, new BoatModel(BoatModel.createBodyModel(false).bakeRoot(), false));
	}

	@Nonnull
	public static <E extends Boat & IBlueprintBoat> BlueprintBoatRenderer<E> chest(EntityRendererProvider.Context context) {
		return new BlueprintBoatRenderer<>(context, new BoatModel(BoatModel.createBodyModel(true).bakeRoot(), true));
	}

	@Override
	public void render(E entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLightIn) {
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
	public ResourceLocation getTextureLocation(E entity) {
		return entity.getTexture();
	}
}