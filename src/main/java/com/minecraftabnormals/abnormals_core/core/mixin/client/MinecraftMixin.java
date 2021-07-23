package com.minecraftabnormals.abnormals_core.core.mixin.client;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public final class MinecraftMixin {
	@Shadow
	public ClientWorld level;
	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	@Final
	public IngameGui gui;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/overlay/BossOverlayGui;shouldPlayMusic()Z"), method = "getSituationalMusic", cancellable = true)
	private void addCustomEndBiomeMusic(CallbackInfoReturnable<BackgroundMusicSelector> info) {
		if (!this.gui.getBossOverlay().shouldPlayMusic()) {
			Biome biome = this.level.getBiomeManager().getNoiseBiomeAtPosition(this.player.blockPosition());
			if (BiomeUtil.shouldPlayCustomEndMusic(biome.getRegistryName())) {
				info.setReturnValue(biome.getBackgroundMusic().orElse(BackgroundMusicTracks.GAME));
			}
		}
	}
}
