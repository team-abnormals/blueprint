package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModificationManager;
import com.teamabnormals.blueprint.common.loot.modification.LootModificationManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public final class ReloadableServerResourcesMixin {

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"), method = "loadResources", locals = LocalCapture.CAPTURE_FAILHARD)
	private static void addModificationManagers(ResourceManager p_206862_, RegistryAccess.Frozen p_206863_, Commands.CommandSelection p_206864_, int p_206865_, Executor p_206866_, Executor p_206867_, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> info, ReloadableServerResources reloadableServerResources, List<PreparableReloadListener> listeners) {
		PredicateManager predicateManager = reloadableServerResources.getPredicateManager();
		LootModificationManager.add(listeners, predicateManager);
		AdvancementModificationManager.add(listeners, predicateManager);
	}

}
