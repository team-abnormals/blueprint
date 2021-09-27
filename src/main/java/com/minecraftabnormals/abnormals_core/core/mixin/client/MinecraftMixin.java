package com.minecraftabnormals.abnormals_core.core.mixin.client;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public final class MinecraftMixin {
	@Shadow
	public ClientLevel level;
	@Shadow
	public LocalPlayer player;
	@Shadow
	@Final
	public Gui gui;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;shouldPlayMusic()Z"), method = "getSituationalMusic", cancellable = true)
	private void addCustomEndBiomeMusic(CallbackInfoReturnable<Music> info) {
		if (!this.gui.getBossOverlay().shouldPlayMusic()) {
			Biome biome = this.level.getBiomeManager().getNoiseBiomeAtPosition(this.player.blockPosition());
			if (BiomeUtil.shouldPlayCustomEndMusic(biome.getRegistryName())) {
				info.setReturnValue(biome.getBackgroundMusic().orElse(Musics.GAME));
			}
		}
	}
}
