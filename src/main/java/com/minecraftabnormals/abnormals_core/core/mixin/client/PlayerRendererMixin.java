package com.minecraftabnormals.abnormals_core.core.mixin.client;

import com.minecraftabnormals.abnormals_core.client.RewardHandler;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerRenderer.class)
public final class PlayerRendererMixin {

	@Inject(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;pushPose()V", shift = At.Shift.AFTER))
	public void moveName(AbstractClientPlayer entity, Component name, PoseStack stack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		RewardHandler.RewardProperties properties = RewardHandler.getRewardProperties();
		if (properties == null)
			return;

		RewardHandler.RewardProperties.SlabfishProperties slabfishProperties = properties.getSlabfishProperties();
		if (slabfishProperties == null)
			return;

		if (!RewardHandler.SlabfishSetting.getSetting((IDataManager) entity, RewardHandler.SlabfishSetting.ENABLED))
			return;

		UUID uuid = entity.getUUID();
		if (!RewardHandler.REWARDS.containsKey(uuid))
			return;

		RewardHandler.RewardData reward = RewardHandler.REWARDS.get(uuid);
		RewardHandler.RewardData.SlabfishData slabfish = reward.getSlabfish();
		int tier = reward.getTier();

		if (slabfish == null || tier < 2 || (slabfish.getTypeUrl() == null && tier > 3 && slabfishProperties.getDefaultTypeUrl() == null) ||  slabfishProperties.getDefaultTypeUrl() == null)
			return;

		stack.translate(0, 0.5, 0);
	}
	
}
