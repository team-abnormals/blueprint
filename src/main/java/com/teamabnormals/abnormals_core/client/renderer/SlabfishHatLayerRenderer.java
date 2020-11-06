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
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class SlabfishHatLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	private final SlabfishHatModel model;

	public SlabfishHatLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderer, SlabfishHatModel slabfishModel) {
		super(renderer);
		this.model = slabfishModel;
	}

	@Override
	public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		String defaultTypeUrl = RewardHandler.getRewardProperties().getSlabfishProperties().getDefaultTypeUrl();
		IDataManager data = (IDataManager) entity;

		if (!data.getValue(AbnormalsCore.SLABFISH_HEAD) || defaultTypeUrl == null || !RewardHandler.REWARDS.containsKey(entity.getUniqueID()) || entity.isInvisible() || entity.isSpectator())
			return;

		RewardHandler.RewardData reward = RewardHandler.REWARDS.get(entity.getUniqueID());

		if(reward.getSlabfish() == null || reward.getTier() < 2)
			return;

		RewardHandler.RewardData.SlabfishData slabfish = reward.getSlabfish();
		ResourceLocation typeLocation = RewardHandler.REWARD_CACHE.getTextureLocation(reward.getTier() >= 4 && slabfish.getTypeUrl() != null ? slabfish.getTypeUrl() : defaultTypeUrl);
		ResourceLocation sweaterLocation = reward.getTier() >= 3 && slabfish.getSweaterUrl() != null ? RewardHandler.REWARD_CACHE.getTextureLocation(slabfish.getSweaterUrl()) : null;
		ResourceLocation backpackLocation = reward.getTier() >= 2 && slabfish.getBackpackUrl() != null ? RewardHandler.REWARD_CACHE.getTextureLocation(slabfish.getBackpackUrl()) : null;

		if(typeLocation == null)
			return;

		this.model.body.copyModelAngles(this.getEntityModel().bipedHead);
		this.model.body.render(stack, buffer.getBuffer(RenderType.getEntityCutout(typeLocation)), packedLight, OverlayTexture.NO_OVERLAY);

		if(sweaterLocation != null)
			this.model.body.render(stack, buffer.getBuffer(RenderType.getEntityCutout(sweaterLocation)), packedLight, OverlayTexture.NO_OVERLAY);

		if(backpackLocation != null) {
			this.model.backpack.copyModelAngles(this.model.body);
			this.model.backpack.render(stack, buffer.getBuffer(RenderType.getEntityCutout(backpackLocation)), packedLight, OverlayTexture.NO_OVERLAY);
		}
	}
}
