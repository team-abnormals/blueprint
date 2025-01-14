package com.teamabnormals.blueprint.common.world.storage.tracking;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class works as an information holder for a type to be tracked. <p>This class keeps track of the following information: </p>
 * <p>This data's {@link MapCodec} for writing/reading saved NBT data.</p>
 * <p>This data's {@link StreamCodec} for writing/reading packet data.</p>
 * <p>A {@link Supplier} representing the default value getter for this data.</p>
 * <p>A {@link SyncType} for how this data should be synced.</p>
 * <p>A boolean, {@link #persistent}, if this data should be wiped when a data-clearing event occurs, e.g. a player dying.</p>
 *
 * @param <T> The type to track.
 * @author SmellyModder (Luke Tonon)
 */
public class TrackedData<T> {
	@Nullable
	private final MapCodec<T> codec;
	private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
	private final Supplier<T> defaultValue;
	private final SyncType syncType;
	private final boolean persistent;

	private TrackedData(@Nullable MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> defaultValue, SyncType syncType, boolean persistent) {
		this.codec = codec;
		this.streamCodec = streamCodec;
		this.defaultValue = defaultValue;
		this.syncType = syncType;
		this.persistent = persistent;
	}

	/**
	 * Gets this data's {@link #codec}.
	 *
	 * @return This data's {@link #codec}.
	 */
	@Nullable
	public MapCodec<T> getCodec() {
		return this.codec;
	}

	/**
	 * Gets this data's {@link #streamCodec}.
	 *
	 * @return This data's {@link #streamCodec}.
	 */
	public StreamCodec<? super RegistryFriendlyByteBuf, T> getStreamCodec() {
		return streamCodec;
	}

	/**
	 * Gets this data's {@link #syncType}.
	 *
	 * @return This data's {@link #syncType}.
	 */
	public SyncType getSyncType() {
		return this.syncType;
	}

	/**
	 * Gets the value of this data's {@link #defaultValue}.
	 *
	 * @return The value of this data's {@link #defaultValue}.
	 */
	public T getDefaultValue() {
		return this.defaultValue.get();
	}

	/**
	 * Checks if this data is persistent.
	 *
	 * @return If this data is persistent.
	 */
	public boolean isPersistent() {
		return this.persistent;
	}

	/**
	 * The builder class for {@link TrackedData}.
	 *
	 * @param <T> The type to track.
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class Builder<T> {
		@Nullable
		private MapCodec<T> codec;
		private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
		private final Supplier<T> defaultValue;
		private SyncType syncType;
		private boolean persistent;

		private Builder(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, final Supplier<T> defaultValue) {
			this.streamCodec = streamCodec;
			this.defaultValue = defaultValue;
			this.syncType = SyncType.TO_CLIENTS;
		}

		/**
		 * Creates a builder for a new {@link TrackedData} instance.
		 *
		 * @param streamCodec The stream codec to use for serializing and deserializing.
		 * @param <T>         The type of data to track.
		 * @return This current builder.
		 */
		public static <T> Builder<T> create(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, final Supplier<T> defaultValue) {
			return new Builder<>(streamCodec, defaultValue);
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
		 * Enables NBT writing and reading by assigning a codec to perform saving and loading.
		 *
		 * @param codec The codec to perform saving and loading.
		 * @return This current builder.
		 */
		public Builder<T> enableSaving(MapCodec<T> codec) {
			this.codec = codec;
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
			return new TrackedData<>(this.codec, this.streamCodec, this.defaultValue, this.syncType, this.persistent);
		}
	}
}
