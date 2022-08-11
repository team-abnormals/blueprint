package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlicesManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public final class IntegratedServerMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;loadLevel()V", shift = At.Shift.BEFORE), method = "initServer")
	private void initServer(CallbackInfoReturnable<Boolean> info) {
		ModdedBiomeSlicesManager.onServerAboutToStart((MinecraftServer) (Object) this);
	}
}