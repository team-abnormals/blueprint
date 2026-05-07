package com.teamabnormals.blueprint.core.mixin.client;

import com.mojang.serialization.Lifecycle;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class)
public final class WorldOpenFlowsMixin {

	@Inject(method = "confirmWorldCreation", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/serialization/Lifecycle;experimental()Lcom/mojang/serialization/Lifecycle;", remap = false), cancellable = true)
	private static void confirmWorldCreation(Minecraft minecraft, CreateWorldScreen screen, Lifecycle lifecycle, Runnable loadWorld, boolean skipWarnings, CallbackInfo ci) {
		if (BlueprintConfig.CLIENT.disableExperimentalSettingsScreen) {
			loadWorld.run();
			ci.cancel();
		}
	}
}