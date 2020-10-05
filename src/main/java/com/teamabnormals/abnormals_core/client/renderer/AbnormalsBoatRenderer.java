package com.teamabnormals.abnormals_core.client.renderer;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamabnormals.abnormals_core.common.entity.AbnormalsBoatEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AbnormalsBoatRenderer extends EntityRenderer<AbnormalsBoatEntity> {
	private final AbnormalsBoatModel model = new AbnormalsBoatModel();

	public AbnormalsBoatRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		this.shadowSize = 0.8F;
	}

	@Override
	public void render(AbnormalsBoatEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStackIn.push();
		matrixStackIn.translate(0.0D, 0.375D, 0.0D);
		matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
		float f = (float) entity.getTimeSinceHit() - partialTicks;
		float f1 = entity.getDamageTaken() - partialTicks;
		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f > 0.0F) {
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.sin(f) * f * f1 / 10.0F * (float) entity.getForwardDirection()));
		}

		float f2 = entity.getRockingAngle(partialTicks);
		if (!MathHelper.epsilonEquals(f2, 0.0F)) {
			matrixStackIn.rotate(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), entity.getRockingAngle(partialTicks), true));
		}

		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
		this.model.setRotationAngles(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(this.getEntityTexture(entity)));
		this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.getWaterMask());
		this.model.func_228245_c_().render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY);
		matrixStackIn.pop();
		super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getEntityTexture(AbnormalsBoatEntity entity) {
		return entity.getBoat().getTexture();
	}

	@OnlyIn(Dist.CLIENT)
	public class AbnormalsBoatModel extends SegmentedModel<AbnormalsBoatEntity> {
		private final ModelRenderer[] paddles = new ModelRenderer[2];
		private final ModelRenderer noWater;
		private final ImmutableList<ModelRenderer> field_228243_f_;

		public AbnormalsBoatModel() {
			ModelRenderer[] amodelrenderer = new ModelRenderer[]{(new ModelRenderer(this, 0, 0)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 27)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 35)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 43)).setTextureSize(128, 64)};
			amodelrenderer[0].addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
			amodelrenderer[0].setRotationPoint(0.0F, 3.0F, 1.0F);
			amodelrenderer[1].addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[1].setRotationPoint(-15.0F, 4.0F, 4.0F);
			amodelrenderer[2].addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[2].setRotationPoint(15.0F, 4.0F, 0.0F);
			amodelrenderer[3].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[3].setRotationPoint(0.0F, 4.0F, -9.0F);
			amodelrenderer[4].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
			amodelrenderer[4].setRotationPoint(0.0F, 4.0F, 9.0F);
			amodelrenderer[0].rotateAngleX = ((float) Math.PI / 2F);
			amodelrenderer[1].rotateAngleY = ((float) Math.PI * 1.5F);
			amodelrenderer[2].rotateAngleY = ((float) Math.PI / 2F);
			amodelrenderer[3].rotateAngleY = (float) Math.PI;
			this.paddles[0] = this.makePaddle(true);
			this.paddles[0].setRotationPoint(3.0F, -5.0F, 9.0F);
			this.paddles[1] = this.makePaddle(false);
			this.paddles[1].setRotationPoint(3.0F, -5.0F, -9.0F);
			this.paddles[1].rotateAngleY = (float) Math.PI;
			this.paddles[0].rotateAngleZ = 0.19634955F;
			this.paddles[1].rotateAngleZ = 0.19634955F;
			this.noWater = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
			this.noWater.addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
			this.noWater.setRotationPoint(0.0F, -3.0F, 1.0F);
			this.noWater.rotateAngleX = ((float) Math.PI / 2F);
			Builder<ModelRenderer> builder = ImmutableList.builder();
			builder.addAll(Arrays.asList(amodelrenderer));
			builder.addAll(Arrays.asList(this.paddles));
			this.field_228243_f_ = builder.build();
		}

		public void setRotationAngles(AbnormalsBoatEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			this.func_228244_a_(entityIn, 0, limbSwing);
			this.func_228244_a_(entityIn, 1, limbSwing);
		}

		public ImmutableList<ModelRenderer> getParts() {
			return this.field_228243_f_;
		}

		public ModelRenderer func_228245_c_() {
			return this.noWater;
		}

		protected ModelRenderer makePaddle(boolean p_187056_1_) {
			ModelRenderer modelrenderer = (new ModelRenderer(this, 62, p_187056_1_ ? 0 : 20)).setTextureSize(128, 64);
			modelrenderer.addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F);
			modelrenderer.addBox(p_187056_1_ ? -1.001F : 0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F);
			return modelrenderer;
		}

		protected void func_228244_a_(AbnormalsBoatEntity p_228244_1_, int p_228244_2_, float p_228244_3_) {
			float f = p_228244_1_.getRowingTime(p_228244_2_, p_228244_3_);
			ModelRenderer modelrenderer = this.paddles[p_228244_2_];
			modelrenderer.rotateAngleX = (float) MathHelper.clampedLerp((double) (-(float) Math.PI / 3F), (double) -0.2617994F, (double) ((MathHelper.sin(-f) + 1.0F) / 2.0F));
			modelrenderer.rotateAngleY = (float) MathHelper.clampedLerp((double) (-(float) Math.PI / 4F), (double) ((float) Math.PI / 4F), (double) ((MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F));
			if (p_228244_2_ == 1) {
				modelrenderer.rotateAngleY = (float) Math.PI - modelrenderer.rotateAngleY;
			}
		}
	}
}