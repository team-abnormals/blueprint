package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.remolder.RemoldableResourceManager;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftServer.class)
public final class MinecraftServerMixin {
	// TODO: Maybe simplify this
	@Dynamic
	@ModifyVariable(method = "*(Lcom/google/common/collect/ImmutableList;)Ljava/util/concurrent/CompletionStage;", index = 2, at = @At(value = "STORE", ordinal = 0))
	private CloseableResourceManager reloadRemolders(CloseableResourceManager manager) {
		if (manager instanceof RemoldableResourceManager remoldableResourceManager) {
			remoldableResourceManager.updateRemolderLoader(PackType.SERVER_DATA, false).reloadRemolders(remoldableResourceManager, Util.backgroundExecutor());
		}
		return manager;
	}
}
