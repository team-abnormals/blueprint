package com.teamabnormals.blueprint.common.world.storage.tracking;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * This class is basically an external version of the {@link net.minecraft.network.syncher.SynchedEntityData} System.
 * <p>
 * The {@link net.minecraft.network.syncher.SynchedEntityData} System is used in Minecraft to sync data on entities from the server to clients.
 * This system is very similar to the {@link net.minecraft.network.syncher.SynchedEntityData} System with a few differences.
 * One difference being all reading and writing is done with NBT so it can be used for both networking and storage.
 * These differences and advantages may make these more favorable than capabilities depending on the use case.
 * </p>
 * <p> Another important detail is this system can be applied to any other object, but you must do the groundwork yourself. </p>
 * <p> To register a {@link TrackedData} use {@link #registerData(ResourceLocation, TrackedData)} during the common setup of your mod. </p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum TrackedDataManager {
	INSTANCE;

	private final BiMap<ResourceLocation, TrackedData<?>> dataMap = HashBiMap.create();
	private final BiMap<Integer, TrackedData<?>> idMap = HashBiMap.create();
	private int nextId = 0;

	TrackedDataManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Registers a {@link TrackedData} for a {@link ResourceLocation} key.
	 * Call this in the common setup of your mod.
	 *
	 * @param key         The key to register the {@link TrackedData} for.
	 * @param trackedData The {@link TrackedData} to register.
	 */
	public synchronized void registerData(ResourceLocation key, TrackedData<?> trackedData) {
		if (this.dataMap.containsKey(key)) {
			throw new IllegalArgumentException(String.format("A Tracked Data with key '%s' is already registered!", key));
		}
		this.dataMap.put(key, trackedData);
		this.idMap.put(this.nextId, trackedData);
		this.nextId++;
	}

	/**
	 * Sets a value for a {@link TrackedData} for an {@link Entity}.
	 *
	 * @param entity      The entity to set the value for.
	 * @param trackedData The {@link TrackedData} to set the value for.
	 * @param value       The value to set for the {@link TrackedData}.
	 * @param <T>         The type of value.
	 */
	public <T> void setValue(Entity entity, TrackedData<T> trackedData, T value) {
		if (!this.dataMap.containsValue(trackedData)) {
			throw new IllegalArgumentException(String.format("No key is registered for this Tracked Data: %s", trackedData));
		}
		((IDataManager) entity).setValue(trackedData, value);
	}

	/**
	 * Gets a value for a {@link TrackedData} for an {@link Entity}.
	 *
	 * @param entity      The entity to set the value from.
	 * @param trackedData The {@link TrackedData} to get the value for.
	 * @param <T>         The type of value to get.
	 * @return The value gotten from the {@link TrackedData} from the {@link Entity}.
	 */
	public <T> T getValue(Entity entity, TrackedData<T> trackedData) {
		if (!this.dataMap.containsValue(trackedData)) {
			throw new IllegalArgumentException(String.format("No key is registered for this Tracked Data: %s", trackedData));
		}
		return ((IDataManager) entity).getValue(trackedData);
	}

	/**
	 * Gets a {@link TrackedData} by a {@link ResourceLocation}.
	 *
	 * @param resourceLocation The {@link ResourceLocation} to lookup.
	 * @return The {@link TrackedData} registered for the supplied {@link ResourceLocation}.
	 */
	@Nullable
	public TrackedData<?> getTrackedData(ResourceLocation resourceLocation) {
		return this.dataMap.get(resourceLocation);
	}

	/**
	 * Gets the {@link ResourceLocation} key for a {@link TrackedData}.
	 *
	 * @param trackedData The {@link TrackedData} to lookup.
	 * @return The {@link ResourceLocation} key for the registered {@link TrackedData}.
	 */
	@Nullable
	public ResourceLocation getKey(TrackedData<?> trackedData) {
		return this.dataMap.inverse().get(trackedData);
	}

	/**
	 * Gets a {@link TrackedData} by its registry id.
	 *
	 * @param id The id to lookup.
	 * @return The {@link TrackedData} for the supplied id.
	 */
	@Nullable
	public TrackedData<?> getTrackedData(int id) {
		return this.idMap.get(id);
	}

	/**
	 * Gets the id of a {@link TrackedData}.
	 *
	 * @param trackedData The {@link TrackedData} to get the id for.
	 * @return The id of the supplied id.
	 */
	public int getId(TrackedData<?> trackedData) {
		return this.idMap.inverse().get(trackedData);
	}

	@SubscribeEvent
	public void onStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (!target.level.isClientSide) {
			Set<IDataManager.DataEntry<?>> entries = ((IDataManager) target).getEntries(true);
			if (!entries.isEmpty()) {
				NetworkUtil.updateTrackedData((ServerPlayer) event.getPlayer(), target.getId(), entries);
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity target = event.getEntity();
		if (!target.level.isClientSide) {
			Set<IDataManager.DataEntry<?>> entries = ((IDataManager) target).getEntries(false);
			if (!entries.isEmpty()) {
				NetworkUtil.updateTrackedData(target, entries);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		Player original = event.getOriginal();
		if (!original.level.isClientSide) {
			Map<TrackedData<?>, IDataManager.DataEntry<?>> dataMap = ((IDataManager) original).getDataMap();
			if (event.isWasDeath()) {
				dataMap.entrySet().removeIf(entry -> !entry.getKey().isPersistent());
			}
			dataMap.values().forEach(IDataManager.DataEntry::markDirty);
			((IDataManager) event.getPlayer()).setDataMap(dataMap);
		}
	}

	@SubscribeEvent
	public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		IDataManager dataManager = (IDataManager) event.getPlayer();
		Map<TrackedData<?>, IDataManager.DataEntry<?>> dataMap = dataManager.getDataMap();
		dataMap.values().forEach(IDataManager.DataEntry::markDirty);
		((IDataManager) event.getPlayer()).setDataMap(dataMap);
	}
}
