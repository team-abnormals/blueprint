package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerEntity.class)
public final class ServerEntityMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;hasImpulse:Z", ordinal = 2, shift = At.Shift.AFTER), method = "sendChanges")
	private void sendImpulse(CallbackInfo info) {
		updateEntityData(this.entity);
	}

	@Inject(at = @At("HEAD"), method = "sendChanges")
	private void sendChanges(CallbackInfo info) {
		Entity entity = this.entity;
		IDataManager dataManager = (IDataManager) entity;
		if (dataManager.isDirty()) {
			updateEntityData(entity);
		}
	}

	private static void updateEntityData(Entity entity) {
		IDataManager dataManager = (IDataManager) entity;
		Set<IDataManager.DataEntry<?>> entries = dataManager.getDirtyEntries();
		if (!entries.isEmpty()) {
			if (entity instanceof ServerPlayer) {
				NetworkUtil.updateTrackedData((ServerPlayer) entity, entity.getId(), entries);
			}
			NetworkUtil.updateTrackedData(entity, entries);
		}
		dataManager.clean();
	}
}
