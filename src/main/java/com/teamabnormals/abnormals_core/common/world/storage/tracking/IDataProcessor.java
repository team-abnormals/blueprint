package com.teamabnormals.abnormals_core.common.world.storage.tracking;

import net.minecraft.nbt.CompoundNBT;

/**
 * A simple interface that reads and writes NBT for a type of value.
 * @param <T> The type to write and read.
 * @author SmellyModder (Luke Tonon)
 */
public interface IDataProcessor<T> {
	/**
	 * Writes a type to a {@link CompoundNBT}.
	 * @param type An object of the type to write.
	 * @return The object serialized to {@link CompoundNBT}.
	 */
	CompoundNBT write(T type);

	/**
	 * Reads a type from a {@link CompoundNBT}
	 * @param compound The {@link CompoundNBT} to read.
	 * @return The type deserialized from a {@link CompoundNBT}.
	 */
	T read(CompoundNBT compound);
}
