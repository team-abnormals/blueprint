package com.minecraftabnormals.abnormals_core.core.mixin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.SyncType;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.events.EntityWalkEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(Entity.class)
public final class EntityMixin implements IDataManager {
	@Shadow
	private Level level;

	private Map<TrackedData<?>, DataEntry<?>> dataMap = Maps.newHashMap();
	private boolean dirty = false;

	@SuppressWarnings("unchecked")
	@Override
	public <T> void setValue(TrackedData<T> trackedData, T value) {
		DataEntry<T> entry = (DataEntry<T>) this.dataMap.computeIfAbsent(trackedData, DataEntry::new);
		if (!entry.getValue().equals(value)) {
			boolean dirty = !this.level.isClientSide && entry.getTrackedData().getSyncType() != SyncType.NOPE;
			entry.setValue(value, dirty);
			this.dirty = dirty;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(TrackedData<T> trackedData) {
		return (T) this.dataMap.computeIfAbsent(trackedData, DataEntry::new).getValue();
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public void clean() {
		this.dataMap.values().forEach(DataEntry::clean);
		this.dirty = false;
	}

	@Override
	public void setDataMap(Map<TrackedData<?>, DataEntry<?>> dataMap) {
		this.dirty = true;
		this.dataMap = dataMap;
	}

	@Override
	public Map<TrackedData<?>, DataEntry<?>> getDataMap() {
		return this.dataMap;
	}

	@Override
	public Set<DataEntry<?>> getDirtyEntries() {
		Set<DataEntry<?>> dirtyEntries = Sets.newHashSet();
		for (DataEntry<?> entry : this.dataMap.values()) {
			if (entry.isDirty() && entry.getTrackedData().getSyncType() != SyncType.NOPE) {
				dirtyEntries.add(entry);
			}
		}
		return dirtyEntries;
	}

	@Override
	public Set<DataEntry<?>> getEntries(boolean syncToAll) {
		Set<DataEntry<?>> dirtyEntries = Sets.newHashSet();
		for (DataEntry<?> entry : this.dataMap.values()) {
			SyncType syncType = entry.getTrackedData().getSyncType();
			if (syncToAll ? syncType == SyncType.TO_CLIENTS : syncType != SyncType.NOPE) {
				dirtyEntries.add(entry);
			}
		}
		return dirtyEntries;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V", shift = At.Shift.BEFORE), method = "saveWithoutId")
	private void writeTrackedData(CompoundTag compound, CallbackInfoReturnable<CompoundTag> info) {
		if (!this.dataMap.isEmpty()) {
			ListTag dataListTag = new ListTag();
			this.dataMap.forEach((trackedData, dataEntry) -> {
				if (trackedData.shouldSave()) {
					CompoundTag dataTag = dataEntry.writeValue();
					dataTag.putString("Id", TrackedDataManager.INSTANCE.getKey(trackedData).toString());
					dataListTag.add(dataTag);
				}
			});
			compound.put("ACTrackedData", dataListTag);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V", shift = At.Shift.BEFORE), method = "load")
	public void read(CompoundTag compound, CallbackInfo info) {
		if (compound.contains("ACTrackedData", Constants.NBT.TAG_LIST)) {
			ListTag dataListTag = compound.getList("ACTrackedData", Constants.NBT.TAG_COMPOUND);
			dataListTag.forEach(nbt -> {
				CompoundTag dataTag = (CompoundTag) nbt;
				ResourceLocation id = new ResourceLocation(dataTag.getString("Id"));
				TrackedData<?> trackedData = TrackedDataManager.INSTANCE.getTrackedData(id);
				if (trackedData != null && trackedData.shouldSave()) {
					IDataManager.DataEntry<?> dataEntry = new DataEntry<>(trackedData);
					dataEntry.readValue(dataTag, true);
					this.dataMap.put(trackedData, dataEntry);
				} else if (trackedData == null) {
					AbnormalsCore.LOGGER.warn("Received NBT for unknown Tracked Data: {}", id);
				}
			});
		}
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;stepOn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V"))
	private void onEntityWalk(Block block, Level world, BlockPos pos, Entity entity) {
		if (!EntityWalkEvent.onEntityWalk(world, pos, entity)) {
			block.stepOn(world, pos, entity);
		}
	}
}
