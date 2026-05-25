package com.teamabnormals.blueprint.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModificationManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public final class ReloadableServerResourcesMixin {
	@Shadow
	@Final
	private ServerAdvancementManager advancements;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initModificationManagers(RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection selection, int functionCompilationLevel, CallbackInfo info) {
		AdvancementModificationManager.INSTANCE = new AdvancementModificationManager(frozen.createSerializationContext(JsonOps.INSTANCE));
	}

	@ModifyReturnValue(method = "listeners", at = @At("RETURN"))
	private List<PreparableReloadListener> insertListeners(List<PreparableReloadListener> listeners) {
		if (AdvancementModificationManager.INSTANCE != null) {
			int indexOfAdvancements = listeners.indexOf(this.advancements);
			if (indexOfAdvancements != -1)
				(listeners = new ArrayList<>(listeners)).add(indexOfAdvancements, AdvancementModificationManager.INSTANCE);
		}
		return listeners;
	}
}
