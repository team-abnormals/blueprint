package com.teamabnormals.abnormals_core.core.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamabnormals.abnormals_core.client.RewardHandler;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerRenderer.class)
public final class PlayerRendererMixin {

	@Inject(method = "renderName", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;push()V", shift = At.Shift.AFTER))
	public void moveName(AbstractClientPlayerEntity entity, ITextComponent name, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo ci) {
		if (!RewardHandler.SlabfishSetting.getSetting((IDataManager) entity, RewardHandler.SlabfishSetting.ENABLED))
			return;

		UUID uuid = entity.getUniqueID();
		if (!RewardHandler.REWARDS.containsKey(uuid))
			return;

		RewardHandler.RewardData reward = RewardHandler.REWARDS.get(uuid);
		RewardHandler.RewardData.SlabfishData slabfish = reward.getSlabfish();
		RewardHandler.RewardProperties.SlabfishProperties slabfishProperties = RewardHandler.getRewardProperties().getSlabfishProperties();
		int tier = reward.getTier();

		if (slabfish == null || tier < 2 || (slabfish.getTypeUrl() == null && tier > 3 && slabfishProperties.getDefaultTypeUrl() == null) ||  slabfishProperties.getDefaultTypeUrl() == null)
			return;

		stack.translate(0, 0.5, 0);
	}

}