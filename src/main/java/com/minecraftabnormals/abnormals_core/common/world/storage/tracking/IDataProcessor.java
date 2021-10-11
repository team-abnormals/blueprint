package com.minecraftabnormals.abnormals_core.common.world.storage.tracking;

import net.minecraft.nbt.CompoundTag;

/**
 * A simple interface that reads and writes NBT for a type of value.
 *
 * @param <T> The type to write and read.
 * @author SmellyModder (Luke Tonon)
 */
public interface IDataProcessor<T> {
	/**
	 * Writes a type to a {@link CompoundTag}.
	 *
	 * @param type An object of the type to write.
	 * @return The object serialized to {@link CompoundTag}.
	 */
	CompoundTag write(T type);

	/**
	 * Reads a type from a {@link CompoundTag}.
	 *
	 * @param compound The {@link CompoundTag} to read.
	 * @return The type deserialized from a {@link CompoundTag}.
	 */
	T read(CompoundTag compound);
}
