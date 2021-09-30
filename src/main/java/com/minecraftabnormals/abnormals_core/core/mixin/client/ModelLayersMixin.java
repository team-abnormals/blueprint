package com.minecraftabnormals.abnormals_core.core.mixin.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelLayers.class)
public final class ModelLayersMixin {

	//Temporary fix for Forge issue
	@Inject(at = @At("HEAD"), method = "createSignModelName", cancellable = true)
	private static void createSignModelName(WoodType type, CallbackInfoReturnable<ModelLayerLocation> info) {
		ResourceLocation location = new ResourceLocation(type.name());
		info.setReturnValue(new ModelLayerLocation(new ResourceLocation(location.getNamespace(), "entity/signs/" + location.getPath()), "main"));
	}

}
