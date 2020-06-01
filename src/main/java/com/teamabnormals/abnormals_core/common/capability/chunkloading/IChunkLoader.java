package com.teamabnormals.abnormals_core.common.capability.chunkloading;

import net.minecraft.util.math.BlockPos;

/**
 * @author SmellyModder(Luke Tonon)
 */
public interface IChunkLoader {
	public void addPos(BlockPos pos);
	public void removePos(BlockPos pos);
	public void tick();
	public boolean containsPos(BlockPos pos);
}