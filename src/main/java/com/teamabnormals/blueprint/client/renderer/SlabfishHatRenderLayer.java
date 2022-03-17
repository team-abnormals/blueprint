package com.teamabnormals.blueprint.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamabnormals.blueprint.client.RewardHandler;
import com.teamabnormals.blueprint.client.RewardHandler.RewardProperties;
import com.teamabnormals.blueprint.client.model.SlabfishHatModel;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.sonar.OnlineImageCache;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.TimeUnit;

/**
 * The {@link RenderLayer} responsible for the rendering of the slabfish patreon hats.
 * <p>For more information, visit the <a href="https://www.patreon.com/teamabnormals">Patreon</a>></p>
 */
public class SlabfishHatRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public static OnlineImageCache REWARD_CACHE = new OnlineImageCache(Blueprint.MOD_ID, 1, TimeUnit.DAYS);
	private final SlabfishHatModel model;

	public SlabfishHatRenderLayer(PlayerRenderer renderer) {
		super(renderer);
		this.model = new SlabfishHatModel(SlabfishHatModel.createBodyModel().bakeRoot());
	}

	@Override
	public void render(PoseStack stack, MultiBufferSource source, int packedLight, AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		RewardProperties properties = RewardHandler.getRewardProperties();
		if (properties == null)
			return;

		RewardHandler.RewardProperties.SlabfishProperties slabfishProperties = properties.getSlabfishProperties();
		if (slabfishProperties == null)
			return;

		String defaultTypeUrl = slabfishProperties.getDefaultTypeUrl();
		IDataManager data = (IDataManager) entity;

		if (entity.isInvisible() || entity.isSpectator() || !(RewardHandler.SlabfishSetting.getSetting(data, RewardHandler.SlabfishSetting.ENABLED)) || defaultTypeUrl == null || !RewardHandler.REWARDS.containsKey(entity.getUUID()))
			return;

		RewardHandler.RewardData reward = RewardHandler.REWARDS.get(entity.getUUID());

		if (reward.getSlabfish() == null || reward.getTier() < 2)
			return;

		RewardHandler.RewardData.SlabfishData slabfish = reward.getSlabfish();
		ResourceLocation typeLocation = REWARD_CACHE.requestTexture(reward.getTier() >= 4 && slabfish.getTypeUrl() != null && RewardHandler.SlabfishSetting.getSetting(data, RewardHandler.SlabfishSetting.TYPE) ? slabfish.getTypeUrl() : defaultTypeUrl).getNow(null);
		if (typeLocation == null)
			return;
		
		ResourceLocation sweaterLocation = reward.getTier() >= 3 && slabfish.getSweaterUrl() != null && RewardHandler.SlabfishSetting.getSetting(data, RewardHandler.SlabfishSetting.SWEATER) ? REWARD_CACHE.requestTexture(slabfish.getSweaterUrl()).getNow(null) : null;
		ResourceLocation backpackLocation = slabfish.getBackpackUrl() != null && RewardHandler.SlabfishSetting.getSetting(data, RewardHandler.SlabfishSetting.BACKPACK) ? REWARD_CACHE.requestTexture(slabfish.getBackpackUrl()).getNow(null) : null;
		ModelPart body = this.model.body;
		ModelPart backpack = this.model.backpack;

		body.copyFrom(this.getParentModel().head);
		body.render(stack, source.getBuffer(slabfish.isTranslucent() ? RenderType.entityTranslucent(typeLocation) : RenderType.entityCutout(typeLocation)), packedLight, OverlayTexture.NO_OVERLAY);

		if (sweaterLocation != null)
			body.render(stack, source.getBuffer(RenderType.entityCutout(sweaterLocation)), packedLight, OverlayTexture.NO_OVERLAY);

		if (backpackLocation != null) {
			backpack.copyFrom(body);
			backpack.render(stack, source.getBuffer(RenderType.entityCutout(backpackLocation)), packedLight, OverlayTexture.NO_OVERLAY);
		}
	}
}
