package com.teamabnormals.blueprint.common.world.storage.tracking;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;

import java.util.*;

/**
 * This interface handles all the management of the {@link TrackedData}s on an object.
 * This can effectively be used on any object type, but the groundwork must be done yourself.
 * If you wish to have this be used on a type of object other than an entity take a look at how this interface is used for the entity tracking system.
 * <p>This is Mixin'd into {@link net.minecraft.world.entity.Entity}, so casting an {@link net.minecraft.world.entity.Entity} to this interface is safe.</p>
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

	/**
	 * Gets the map that stores all the {@link TrackedData} and their corresponding {@link DataEntry}s.
	 *
	 * @return The map that stores all the {@link TrackedData} and their corresponding {@link DataEntry}s.
	 */
	Map<TrackedData<?>, DataEntry<?>> getDataMap();

	/**
	 * Sets the map that stores all the {@link TrackedData} and their corresponding {@link DataEntry}s.
	 *
	 * @param dataEntryMap A new data map.
	 */
	void setDataMap(Map<TrackedData<?>, DataEntry<?>> dataEntryMap);

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
	 * A value class for a {@link TrackedData} key.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	class DataEntry<T> {
		public static final StreamCodec<RegistryFriendlyByteBuf, DataEntry<?>> STREAM_CODEC = StreamCodec.of(
				(buf, entry) -> entry.write(buf),
				DataEntry::read
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, List<DataEntry<?>>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
		private final TrackedData<T> trackedData;
		private T value;
		private boolean dirty;

		public DataEntry(TrackedData<T> trackedData) {
			this.trackedData = trackedData;
			this.value = trackedData.getDefaultValue();
		}

		/**
		 * Reads a new entry from a {@link RegistryFriendlyByteBuf} instance.
		 *
		 * @param buffer A {@link RegistryFriendlyByteBuf} to read a new entry from.
		 * @return A new entry from a {@link RegistryFriendlyByteBuf} instance.
		 */
		public static DataEntry<?> read(RegistryFriendlyByteBuf buffer) {
			TrackedData<?> trackedData = TrackedDataManager.INSTANCE.getTrackedData(buffer.readResourceLocation());
			Objects.requireNonNull(trackedData, String.format("Tracked Data does not exist for id %o", trackedData));
			DataEntry<?> entry = new DataEntry<>(trackedData);
			entry.readValue(buffer, true);
			return entry;
		}

		/**
		 * Saves this data to a tag.
		 *
		 * @param tag A {@link CompoundTag} instance to start saving from.
		 * @param ops A {@link RegistryOps} instance to use.
		 * @return A {@link CompoundTag} instance containing the saved data.
		 */
		public CompoundTag encode(CompoundTag tag, RegistryOps<Tag> ops) {
			var result = this.getTrackedData().getCodec().codec().encode(this.value, ops, tag);
			var error = result.error();
			if (error.isPresent()) throw new RuntimeException("Error encoding tracked data: " + error.get());
			if (!(result.result().get() instanceof CompoundTag compoundTag))
				throw new RuntimeException("Tracked data was not encoded as a compound tag");
			return compoundTag;
		}

		/**
		 * Gets this entry's {@link #trackedData}.
		 *
		 * @return This entry's {@link #trackedData}.
		 */
		public TrackedData<T> getTrackedData() {
			return this.trackedData;
		}

		/**
		 * Gets this entry's {@link #value}.
		 *
		 * @return This entry's {@link #value}.
		 */
		public T getValue() {
			return this.value;
		}

		/**
		 * Sets the {@link #value} of this entry.
		 *
		 * @param value A new value.
		 * @param dirty If this entry should now be marked dirty.
		 */
		public void setValue(T value, boolean dirty) {
			this.value = value;
			this.dirty = dirty;
		}

		/**
		 * Marks this entry dirty.
		 */
		public void markDirty() {
			this.dirty = true;
		}

		/**
		 * Checks if this entry is marked dirty.
		 *
		 * @return If this entry is marked dirty.
		 */
		public boolean isDirty() {
			return this.dirty;
		}

		/**
		 * Marks this entry as clean (not dirty).
		 */
		public void clean() {
			this.dirty = false;
		}

		/**
		 * Writes this entry to a {@link FriendlyByteBuf} instance.
		 *
		 * @param buffer A {@link FriendlyByteBuf} to write this entry to.
		 */
		public void write(RegistryFriendlyByteBuf buffer) {
			buffer.writeResourceLocation(Objects.requireNonNull(TrackedDataManager.INSTANCE.getKey(this.trackedData)));
			this.getTrackedData().getStreamCodec().encode(buffer, this.getValue());
		}

		/**
		 * Reads a new {@link #value} for this entry from a {@link RegistryFriendlyByteBuf}.
		 *
		 * @param buffer A {@link RegistryFriendlyByteBuf} to read from.
		 * @param dirty  If this entry should now be marked dirty.
		 */
		public void readValue(RegistryFriendlyByteBuf buffer, boolean dirty) {
			this.value = this.getTrackedData().getStreamCodec().decode(buffer);
			this.dirty = dirty;
		}

		/**
		 * Reads a new {@link #value} for this entry from a {@link CompoundTag}.
		 *
		 * @param tag   A {@link CompoundTag} to read from.
		 * @param ops   A {@link RegistryOps} instance to use.
		 * @param dirty If this entry should now be marked dirty.
		 */
		public void readValue(CompoundTag tag, RegistryOps<Tag> ops, boolean dirty) {
			this.dirty = dirty;
			this.value = this.getTrackedData().getCodec().codec().decode(ops, tag).mapOrElse(Pair::getFirst, error -> {
				Blueprint.LOGGER.error("Error while decoding tracked data {}:\n{}", tag, error.message());
				Blueprint.LOGGER.warn("Using default value instead");
				return this.getTrackedData().getDefaultValue();
			});
		}
	}
}
