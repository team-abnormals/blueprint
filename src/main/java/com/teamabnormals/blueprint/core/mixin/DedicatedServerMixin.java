package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlicesManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public final class DedicatedServerMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadLevel()V", shift = At.Shift.BEFORE), method = "initServer")
	private void initServer(CallbackInfoReturnable<Boolean> info) {
		ModdedBiomeSlicesManager.onServerAboutToStart((MinecraftServer) (Object) this);
	}
}
