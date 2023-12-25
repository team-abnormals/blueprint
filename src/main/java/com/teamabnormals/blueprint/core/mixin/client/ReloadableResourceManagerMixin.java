package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.common.remolder.RemoldedResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public final class ReloadableResourceManagerMixin {
	@Shadow
	private CloseableResourceManager resources;

	@Inject(method = "createReload", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;resources:Lnet/minecraft/server/packs/resources/CloseableResourceManager;", ordinal = 0, shift = At.Shift.AFTER))
	private void wrapResources(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> completableFuture, List<PackResources> packResourcesList, CallbackInfoReturnable<ReloadInstance> info) {
		if (Minecraft.getInstance().getResourceManager() == (Object) this) this.resources = RemoldedResourceManager.wrapForClient(this.resources);
	}
}
