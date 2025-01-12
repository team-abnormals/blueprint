package com.teamabnormals.blueprint.core.mixin;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.remolder.RemoldableResourceManager;
import net.minecraft.Util;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldLoader.PackConfig.class)
public final class WorldLoaderPackConfigMixin {
	@Inject(method = "createResourceManager", at = @At("RETURN"), cancellable = true)
	private void reloadRemolders(CallbackInfoReturnable<Pair<WorldDataConfiguration, CloseableResourceManager>> info) {
		if (info.getReturnValue().getSecond() instanceof RemoldableResourceManager remoldableResourceManager) {
			remoldableResourceManager.updateRemolderLoader(PackType.SERVER_DATA, false).reloadRemolders(remoldableResourceManager, Util.backgroundExecutor());
		}
	}
}
