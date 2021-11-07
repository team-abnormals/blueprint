package com.teamabnormals.blueprint.core.mixin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.common.world.storage.tracking.SyncType;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.events.EntityStepEvent;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
public final class EntityMixin implements IDataManager, Endimatable {
	@Shadow
	private Level level;
	@Shadow
	private Vec3 position;

	private Map<TrackedData<?>, DataEntry<?>> dataMap = Maps.newHashMap();
	private boolean dirty = false;

	private final EndimatedState endimatedState = new EndimatedState(this);

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

	@Override
	public EndimatedState getEndimatedState() {
		return this.endimatedState;
	}

	@Override
	public Position getPos() {
		return this.position;
	}

	@Override
	public boolean isActive() {
		return this.isAlive();
	}

	@Shadow
	public boolean isAlive() {
		return false;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.BEFORE), method = "saveWithoutId")
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

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.BEFORE), method = "load")
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
					Blueprint.LOGGER.warn("Received NBT for unknown Tracked Data: {}", id);
				}
			});
		}
	}

	@Inject(at = @At(value = "HEAD", shift = At.Shift.BY, by = 1), method = "baseTick")
	private void baseTick(CallbackInfo info) {
		this.endimateTick();
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;stepOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;)V"))
	private void onEntityWalk(Block block, Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!EntityStepEvent.onEntityStep(level, pos, entity)) {
			block.stepOn(level, pos, state, entity);
		}
	}
}
