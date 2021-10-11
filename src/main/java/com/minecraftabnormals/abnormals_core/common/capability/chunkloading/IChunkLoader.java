package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * The interface used to represent a chunk loader for the chunk loader capability.
 *
 * @author SmellyModder(Luke Tonon)
 */
public interface IChunkLoader extends INBTSerializable<Tag> {
	/**
	 * Force loads the chunk at a given {@link BlockPos}.
	 *
	 * @param pos A position to load a chunk at.
	 */
	void addPos(BlockPos pos);

	/**
	 * Unloads the chunk at a given {@link BlockPos} if it's already being force loaded.
	 *
	 * @param pos A position to unload a chunk at.
	 */
	void removePos(BlockPos pos);

	/**
	 * Updates this loader.
	 */
	void tick();

	/**
	 * Checks if a given {@link BlockPos} is in the currently loaded positions.
	 *
	 * @param pos A position to check.
	 * @return If the given {@link BlockPos} is in the currently loaded positions.
	 */
	boolean containsPos(BlockPos pos);
}