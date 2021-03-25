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
	public ClientWorld world;
	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	@Final
	public IngameGui ingameGUI;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/overlay/BossOverlayGui;shouldPlayEndBossMusic()Z"), method = "getBackgroundMusicSelector", cancellable = true)
	private void addCustomEndBiomeMusic(CallbackInfoReturnable<BackgroundMusicSelector> info) {
		if (!this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic()) {
			Biome biome = this.world.getBiomeManager().getBiomeAtPosition(this.player.getPosition());
			if (BiomeUtil.shouldPlayCustomEndMusic(biome.getRegistryName())) {
				info.setReturnValue(biome.getBackgroundMusic().orElse(BackgroundMusicTracks.WORLD_MUSIC));
			}
		}
	}
}
