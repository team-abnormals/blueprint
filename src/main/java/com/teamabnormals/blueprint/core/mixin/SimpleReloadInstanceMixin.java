package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.remolder.RemoldedResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SimpleReloadInstance.class)
public final class SimpleReloadInstanceMixin {
	@Inject(method = "of", at = @At("HEAD"))
	private static void loadRemolders(ResourceManager resourceManager, List<PreparableReloadListener> preparableReloadListeners, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> completableFuture, CallbackInfoReturnable<SimpleReloadInstance<Void>> info) {
		if (resourceManager instanceof RemoldedResourceManager remoldedResourceManager && remoldedResourceManager.needsAutoReload())
			remoldedResourceManager.reloadRemolders(prepareExecutor);
	}
}
