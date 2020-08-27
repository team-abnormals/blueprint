package com.teamabnormals.abnormals_core.common.capability.chunkloading;

import net.minecraft.util.math.BlockPos;

/**
 * @author SmellyModder(Luke Tonon)
 */
public interface IChunkLoader {
	void addPos(BlockPos pos);
	void removePos(BlockPos pos);
	void tick();
	boolean containsPos(BlockPos pos);
}