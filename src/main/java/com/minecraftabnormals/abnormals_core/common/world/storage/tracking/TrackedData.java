package com.minecraftabnormals.abnormals_core.common.world.storage.tracking;

import java.util.function.Supplier;

/**
 * This class works as an information holder for a type to be tracked. <p>This class keeps track of the following information: </p>
 * <p>This data's {@link IDataProcessor} for writing/reading packet data and saved level data.</p>
 * <p>A {@link Supplier} representing the default value getter for this data.</p>
 * <p>A {@link SyncType} for how this data should be synced.</p>
 * <p>A boolean, {@link #save}, if this data should be saved and reloaded.</p>
 * <p>A boolean, {@link #persistent}, if this data should be wiped when a data-clearing event occurs, e.g. a player dying.</p>
 *
 * @param <T> The type to track.
 * @author SmellyModder (Luke Tonon)
 */
public class TrackedData<T> {
	private final IDataProcessor<T> processor;
	private final Supplier<T> defaultValue;
	private final SyncType syncType;
	private final boolean save;
	private final boolean persistent;

	private TrackedData(final IDataProcessor<T> processor, final Supplier<T> defaultValue, final SyncType syncType, final boolean save, final boolean persistent) {
		this.processor = processor;
		this.defaultValue = defaultValue;
		this.syncType = syncType;
		this.save = save;
		this.persistent = persistent;
	}

	public IDataProcessor<T> getProcessor() {
		return this.processor;
	}

	public SyncType getSyncType() {
		return this.syncType;
	}

	public T getDefaultValue() {
		return this.defaultValue.get();
	}

	public boolean shouldSave() {
		return this.save;
	}

	public boolean isPersistent() {
		return this.persistent;
	}

	public static class Builder<T> {
		private final IDataProcessor<T> processor;
		private final Supplier<T> defaultValue;
		private SyncType syncType;
		private boolean save;
		private boolean persistent;

		private Builder(final IDataProcessor<T> processor, final Supplier<T> defaultValue) {
			this.processor = processor;
			this.defaultValue = defaultValue;
			this.syncType = SyncType.TO_CLIENTS;
		}

		/**
		 * Creates a builder for a {@link IDataProcessor}.
		 *
		 * @param processor The processor to use for the {@link TrackedData}.
		 * @param <T>       The type of data to track.
		 * @return This current builder.
		 */
		public static <T> Builder<T> create(final IDataProcessor<T> processor, final Supplier<T> defaultValue) {
			return new Builder<>(processor, defaultValue);
		}

		/**
		 * Sets the {@link SyncType} for the {@link TrackedData} to be built.
		 * <p> This is {@link SyncType#TO_CLIENTS} by default. </p>
		 *
		 * @param syncType The {@link SyncType} to set.
		 * @return This current builder.
		 */
		public Builder<T> setSyncType(SyncType syncType) {
			this.syncType = syncType;
			return this;
		}

		/**
		 * Enables NBT writing and reading.
		 *
		 * @return This current builder.
		 */
		public Builder<T> enableSaving() {
			this.save = true;
			return this;
		}

		/**
		 * Enables data persistence.
		 * When this is enabled for players the data is kept even when the players die.
		 *
		 * @return This current builder.
		 */
		public Builder<T> enablePersistence() {
			this.persistent = true;
			return this;
		}

		/**
		 * Builds the {@link TrackedData}.
		 *
		 * @return A {@link TrackedData} constructed using this builder.
		 */
		public TrackedData<T> build() {
			return new TrackedData<>(this.processor, this.defaultValue, this.syncType, this.save, this.persistent);
		}
	}
}
