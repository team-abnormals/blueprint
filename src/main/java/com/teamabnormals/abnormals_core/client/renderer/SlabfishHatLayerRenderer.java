package com.teamabnormals.abnormals_core.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamabnormals.abnormals_core.client.RewardHandler;
import com.teamabnormals.abnormals_core.client.model.SlabfishHatModel;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.util.ResourceLocation;

public class SlabfishHatLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	public static final ResourceLocation BRUH = MissingTextureSprite.getLocation();
	private final SlabfishHatModel model;

	public SlabfishHatLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderer, SlabfishHatModel slabfishModel) {
		super(renderer);
		this.model = slabfishModel;
	}

	@Override
	public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		IDataManager data = (IDataManager) entity;

		if (entity.isInvisible() || entity.isSpectator() || !data.getValue(AbnormalsCore.SLABFISH_HEAD) || !RewardHandler.REWARDS.containsKey(entity.getUniqueID()))
			return;

		RewardHandler.RewardData reward = RewardHandler.REWARDS.get(entity.getUniqueID());
		int packedOverlay = LivingRenderer.getPackedOverlay(entity, 0);

		if(reward.getTier() >= 4) {
			this.model.body.copyModelAngles(this.getEntityModel().bipedHead);
			this.model.body.render(stack, buffer.getBuffer(RenderType.getEntityCutout(BRUH)), packedLight, packedOverlay);
		}

		if(reward.getTier() >= 3) {
			this.model.sweater.copyModelAngles(this.getEntityModel().bipedHead);
			this.model.sweater.render(stack, buffer.getBuffer(RenderType.getEntityCutout(BRUH)), packedLight, packedOverlay);
		}

		if(reward.getTier() >= 2) {
			this.model.backpack.copyModelAngles(this.getEntityModel().bipedHead);
			this.model.backpack.render(stack, buffer.getBuffer(RenderType.getEntityCutout(BRUH)), packedLight, packedOverlay);
		}
	}
}
