package com.teamabnormals.abnormals_core.common.world.storage.tracking;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This interface handles all the management of the {@link TrackedData}s on an object.
 * This can effectively be used on any object type, but the groundwork must be done yourself.
 * If you wish to have this be used on a type of object other than an entity take a look at how this interface is used for the entity tracking system.
 * <p> This is Mixin'd into {@link net.minecraft.entity.Entity} so casting an {@link net.minecraft.entity.Entity} to it is safe. </p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface IDataManager {
	/**
	 * Sets a value for a {@link TrackedData}.
	 *
	 * @param trackedData The {@link TrackedData} to the the value for.
	 * @param value       The value to set.
	 * @param <T>         The type of value.
	 */
	<T> void setValue(TrackedData<T> trackedData, T value);

	/**
	 * Gets a value for a {@link TrackedData}.
	 *
	 * @param trackedData The {@link TrackedData} to get the value for.
	 * @param <T>         The type of value.
	 */
	<T> T getValue(TrackedData<T> trackedData);

	/**
	 * @return Is this {@link IDataManager} dirty.
	 */
	boolean isDirty();

	/**
	 * Cleans all the dirty entries.
	 */
	void clean();

	void setDataMap(Map<TrackedData<?>, DataEntry<?>> dataEntryMap);

	Map<TrackedData<?>, DataEntry<?>> getDataMap();

	/**
	 * @return The dirty entries.
	 */
	Set<DataEntry<?>> getDirtyEntries();

	/**
	 * @param syncToAll Should this filter for only {@link SyncType#TO_CLIENTS}.
	 * @return The entries for a {@link SyncType}.
	 */
	Set<DataEntry<?>> getEntries(boolean syncToAll);

	/**
	 * A entry class for a {@link TrackedData}.
	 */
	class DataEntry<T> {
		private TrackedData<T> trackedData;
		private T value;
		private boolean dirty;

		public DataEntry(TrackedData<T> trackedData) {
			this.trackedData = trackedData;
			this.value = trackedData.getDefaultValue();
		}

		public TrackedData<T> getTrackedData() {
			return this.trackedData;
		}

		public T getValue() {
			return this.value;
		}

		public void setValue(T value, boolean dirty) {
			this.value = value;
			this.dirty = dirty;
		}

		public void markDirty() {
			this.dirty = true;
		}

		public boolean isDirty() {
			return this.dirty;
		}

		public void clean() {
			this.dirty = false;
		}

		public void write(PacketBuffer buffer) {
			buffer.writeVarInt(TrackedDataManager.INSTANCE.getId(this.trackedData));
			buffer.writeCompoundTag(this.writeValue());
		}

		public static DataEntry<?> read(PacketBuffer buffer) {
			int id = buffer.readVarInt();
			TrackedData<?> trackedData = TrackedDataManager.INSTANCE.getTrackedData(id);
			Objects.requireNonNull(trackedData, String.format("Tracked Data does not exist for id %o", id));
			DataEntry<?> entry = new DataEntry<>(trackedData);
			entry.readValue(buffer.readCompoundTag(), true);
			return entry;
		}

		public CompoundNBT writeValue() {
			return this.getTrackedData().getProcessor().write(this.value);
		}

		public void readValue(CompoundNBT compound, boolean dirty) {
			this.value = this.getTrackedData().getProcessor().read(compound);
			this.dirty = dirty;
		}
	}
}
