package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.remolder.RemoldedResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftServer.class)
public final class MinecraftServerMixin {
	@Dynamic
	@ModifyVariable(method = "*(Lnet/minecraft/core/RegistryAccess$Frozen;Lcom/google/common/collect/ImmutableList;)Ljava/util/concurrent/CompletionStage;", index = 3, at = @At(value = "STORE", ordinal = 0))
	private CloseableResourceManager wrapForRemolders(CloseableResourceManager manager) {
		return RemoldedResourceManager.wrapForServer(manager, true);
	}
}
