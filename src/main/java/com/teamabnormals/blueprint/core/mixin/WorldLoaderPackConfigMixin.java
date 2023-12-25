package com.teamabnormals.blueprint.core.mixin;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.remolder.RemoldedResourceManager;
import net.minecraft.Util;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldLoader.PackConfig.class)
public final class WorldLoaderPackConfigMixin {
	@Inject(method = "createResourceManager", at = @At("RETURN"), cancellable = true)
	private void loadRemolders(CallbackInfoReturnable<Pair<WorldDataConfiguration, CloseableResourceManager>> info) {
		var pair = info.getReturnValue();
		RemoldedResourceManager remoldedResourceManager = RemoldedResourceManager.wrapForServer(pair.getSecond(), false);
		remoldedResourceManager.reloadRemolders(Util.backgroundExecutor());
		info.setReturnValue(Pair.of(pair.getFirst(), remoldedResourceManager));
	}
}
