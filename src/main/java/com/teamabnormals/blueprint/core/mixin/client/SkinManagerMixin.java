package com.teamabnormals.blueprint.core.mixin.client;

import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.teamabnormals.blueprint.client.RewardHandler;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(SkinManager.class)
public final class SkinManagerMixin {
	@Inject(method = "registerTextures", at = @At("RETURN"))
	private void registerRewardPlayerSkinTextures(UUID uuid, MinecraftProfileTextures profileTextures, CallbackInfoReturnable<CompletableFuture<PlayerSkin>> info) {
		if (RewardHandler.REWARDS.containsKey(uuid) && RewardHandler.REWARDS.get(uuid).getTier() >= 99) {
			var playerSkinFuture = info.getReturnValue();
			info.setReturnValue(playerSkinFuture.thenApply(playerSkin -> {
				if (playerSkin.capeTexture() == null)
					return new PlayerSkin(playerSkin.texture(), playerSkin.textureUrl(), RewardHandler.CAPE_TEXTURE, RewardHandler.CAPE_TEXTURE, playerSkin.model(), playerSkin.secure());
				return playerSkin;
			}));
		}
	}
}
