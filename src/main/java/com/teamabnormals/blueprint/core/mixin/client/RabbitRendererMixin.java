package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.core.api.BlueprintRabbitTypes;
import com.teamabnormals.blueprint.core.api.BlueprintRabbitTypes.BlueprintRabbitType;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitRenderer.class)
public abstract class RabbitRendererMixin {
	@Shadow
	@Final
	private static ResourceLocation RABBIT_TOAST_LOCATION;

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Rabbit;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
	private void getTextureLocation(Rabbit rabbit, CallbackInfoReturnable<ResourceLocation> cir) {
		if (!cir.getReturnValue().equals(RABBIT_TOAST_LOCATION)) {
			for (BlueprintRabbitType rabbitType : BlueprintRabbitTypes.values()) {
				if (rabbit.getRabbitType() == rabbitType.id()) {
					cir.setReturnValue(rabbitType.textureLocation());
					break;
				}
			}
		}
	}
}