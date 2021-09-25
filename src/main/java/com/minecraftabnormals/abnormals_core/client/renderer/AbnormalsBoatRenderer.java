package com.minecraftabnormals.abnormals_core.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.minecraftabnormals.abnormals_core.common.entity.AbnormalsBoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class AbnormalsBoatRenderer extends EntityRenderer<AbnormalsBoatEntity> {
	private final AbnormalsBoatModel model = new AbnormalsBoatModel();

	public AbnormalsBoatRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
		this.shadowRadius = 0.8F;
	}

	@Override
	public void render(AbnormalsBoatEntity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.0D, 0.375D, 0.0D);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
		float f = (float) entity.getHurtTime() - partialTicks;
		float f1 = entity.getDamage() - partialTicks;
		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f > 0.0F) {
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) entity.getHurtDir()));
		}

		float f2 = entity.getBubbleAngle(partialTicks);
		if (!Mth.equal(f2, 0.0F)) {
			matrixStackIn.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), entity.getBubbleAngle(partialTicks), true));
		}

		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		this.model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
		this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(RenderType.waterMask());
		this.model.waterPatch().render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY);
		matrixStackIn.popPose();
		super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(AbnormalsBoatEntity entity) {
		return entity.getBoat().getTexture();
	}

	@OnlyIn(Dist.CLIENT)
	public class AbnormalsBoatModel extends ListModel<AbnormalsBoatEntity> {
		private final ModelPart[] paddles = new ModelPart[2];
		private final ModelPart noWater;
		private final ImmutableList<ModelPart> parts;

		public AbnormalsBoatModel() {
			ModelPart[] amodelrenderer = new ModelPart[]{(new ModelPart(this, 0, 0)).setTexSize(128, 64), (new ModelPart(this, 0, 19)).setTexSize(128, 64), (new ModelPart(this, 0, 27)).setTexSize(128, 64), (new ModelPart(this, 0, 35)).setTexSize(128, 64), (new ModelPart(this, 0, 43)).setTexSize(128, 64)};
			amodelrenderer[0].addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
			amodelrenderer[0].setPos(0.0F, 3.0F, 1.0F);
			amodelrenderer[1].addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[1].setPos(-15.0F, 4.0F, 4.0F);
			amodelrenderer[2].addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[2].setPos(15.0F, 4.0F, 0.0F);
			amodelrenderer[3].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[3].setPos(0.0F, 4.0F, -9.0F);
			amodelrenderer[4].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[4].setPos(0.0F, 4.0F, 9.0F);
			amodelrenderer[0].xRot = ((float) Math.PI / 2F);
			amodelrenderer[1].yRot = ((float) Math.PI * 1.5F);
			amodelrenderer[2].yRot = ((float) Math.PI / 2F);
			amodelrenderer[3].yRot = (float) Math.PI;
			this.paddles[0] = this.makePaddle(true);
			this.paddles[0].setPos(3.0F, -5.0F, 9.0F);
			this.paddles[1] = this.makePaddle(false);
			this.paddles[1].setPos(3.0F, -5.0F, -9.0F);
			this.paddles[1].yRot = (float) Math.PI;
			this.paddles[0].zRot = 0.19634955F;
			this.paddles[1].zRot = 0.19634955F;
			this.noWater = (new ModelPart(this, 0, 0)).setTexSize(128, 64);
			this.noWater.addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
			this.noWater.setPos(0.0F, -3.0F, 1.0F);
			this.noWater.xRot = ((float) Math.PI / 2F);
			Builder<ModelPart> builder = ImmutableList.builder();
			builder.addAll(Arrays.asList(amodelrenderer));
			builder.addAll(Arrays.asList(this.paddles));
			this.parts = builder.build();
		}

		public void setupAnim(AbnormalsBoatEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			this.animatePaddle(entityIn, 0, limbSwing);
			this.animatePaddle(entityIn, 1, limbSwing);
		}

		public ImmutableList<ModelPart> parts() {
			return this.parts;
		}

		public ModelPart waterPatch() {
			return this.noWater;
		}

		protected ModelPart makePaddle(boolean p_187056_1_) {
			ModelPart modelrenderer = (new ModelPart(this, 62, p_187056_1_ ? 0 : 20)).setTexSize(128, 64);
			modelrenderer.addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F);
			modelrenderer.addBox(p_187056_1_ ? -1.001F : 0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F);
			return modelrenderer;
		}

		protected void animatePaddle(AbnormalsBoatEntity p_228244_1_, int p_228244_2_, float p_228244_3_) {
			float f = p_228244_1_.getRowingTime(p_228244_2_, p_228244_3_);
			ModelPart modelrenderer = this.paddles[p_228244_2_];
			modelrenderer.xRot = (float) Mth.clampedLerp((double) (-(float) Math.PI / 3F), (double) -0.2617994F, (double) ((Mth.sin(-f) + 1.0F) / 2.0F));
			modelrenderer.yRot = (float) Mth.clampedLerp((double) (-(float) Math.PI / 4F), (double) ((float) Math.PI / 4F), (double) ((Mth.sin(-f + 1.0F) + 1.0F) / 2.0F));
			if (p_228244_2_ == 1) {
				modelrenderer.yRot = (float) Math.PI - modelrenderer.yRot;
			}
		}
	}
}