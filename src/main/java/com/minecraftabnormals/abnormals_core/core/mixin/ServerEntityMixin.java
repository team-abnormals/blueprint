package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.events.EntityChangedEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public final class ServerEntityMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;hasImpulse:Z", ordinal = 2, shift = At.Shift.AFTER), method = "sendChanges")
	private void sendImpulse(CallbackInfo info) {
		EntityChangedEvent.onEntitySendChanges(this.entity, true);
	}

	@Inject(at = @At("HEAD"), method = "sendChanges")
	private void sendChanges(CallbackInfo info) {
		EntityChangedEvent.onEntitySendChanges(this.entity, false);
	}
}
