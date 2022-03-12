package com.teamabnormals.blueprint.core.mixin.client;

import com.mojang.serialization.Lifecycle;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PrimaryLevelData.class)
public final class PrimaryLevelDataMixin {

	@Inject(method = "worldGenSettingsLifecycle", at = @At("HEAD"), cancellable = true)
	private void forceStableLifecycle(CallbackInfoReturnable<Lifecycle> info) {
		if (BlueprintConfig.CLIENT.disableExperimentalSettingsScreen) info.setReturnValue(Lifecycle.stable());
	}

}
