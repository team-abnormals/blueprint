package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author SmellyModder(Luke Tonon)
 */
public interface IChunkLoader extends INBTSerializable<Tag> {
	void addPos(BlockPos pos);

	void removePos(BlockPos pos);

	void tick();

	boolean containsPos(BlockPos pos);
}