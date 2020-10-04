package com.teamabnormals.abnormals_core.core.mixin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.SyncType;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(Entity.class)
public final class EntityMixin implements IDataManager {
	@Shadow
	private World world;

	private Map<TrackedData<?>, DataEntry<?>> dataMap = Maps.newHashMap();
	private boolean dirty = false;

	@SuppressWarnings("unchecked")
	@Override
	public <T> void setValue(TrackedData<T> trackedData, T value) {
		DataEntry<T> entry = (DataEntry<T>) this.dataMap.computeIfAbsent(trackedData, DataEntry::new);
		if (!entry.getValue().equals(value)) {
			boolean dirty = !this.world.isRemote && entry.getTrackedData().getSyncType() != SyncType.NOPE;
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

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", shift = At.Shift.BEFORE), method = "writeWithoutTypeId")
	private void writeTrackedData(CompoundNBT compound, CallbackInfoReturnable<CompoundNBT> info) {
		if (!this.dataMap.isEmpty()) {
			ListNBT dataListTag = new ListNBT();
			this.dataMap.forEach((trackedData, dataEntry) -> {
				if (trackedData.shouldSave()) {
					CompoundNBT dataTag = dataEntry.writeValue();
					dataTag.putString("Id", TrackedDataManager.INSTANCE.getKey(trackedData).toString());
					dataListTag.add(dataTag);
				}
			});
			compound.put("ACTrackedData", dataListTag);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", shift = At.Shift.BEFORE), method = "read")
	public void read(CompoundNBT compound, CallbackInfo info) {
		if (compound.contains("ACTrackedData", Constants.NBT.TAG_LIST)) {
			ListNBT dataListTag = compound.getList("ACTrackedData", Constants.NBT.TAG_COMPOUND);
			dataListTag.forEach(nbt -> {
				CompoundNBT dataTag = (CompoundNBT) nbt;
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
}
