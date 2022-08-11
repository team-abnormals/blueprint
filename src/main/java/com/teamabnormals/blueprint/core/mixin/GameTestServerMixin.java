package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlicesManager;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameTestServer.class)
public final class GameTestServerMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/gametest/framework/GameTestServer;loadLevel()V", shift = At.Shift.BEFORE), method = "initServer")
	private void initServer(CallbackInfoReturnable<Boolean> info) {
		ModdedBiomeSlicesManager.onServerAboutToStart((MinecraftServer) (Object) this);
	}
}
