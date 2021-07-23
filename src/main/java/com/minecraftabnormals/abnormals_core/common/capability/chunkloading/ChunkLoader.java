package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.minecraftabnormals.abnormals_core.core.util.TickTask;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;

/**
 * Can load and unload Chunks, as well as schedule tick tasks on Chunks
 *
 * @author SmellyModder(Luke Tonon)
 */
public class ChunkLoader implements IChunkLoader {
	@Nullable
	private final ServerWorld world;
	public final List<Long> loadedPositions = Lists.newArrayList();
	private final List<TickTask<IChunk>> scheduledChunkProcesses = Lists.newArrayList();

	public ChunkLoader(@Nullable ServerWorld world) {
		this.world = world;
	}

	@Override
	public void addPos(BlockPos pos) {
		if (!this.containsPos(pos)) {
			this.forceChunk(pos, true);
			this.loadedPositions.add(pos.asLong());
		}
	}

	@Override
	public void removePos(BlockPos pos) {
		if (this.loadedPositions.remove(pos.asLong())) {
			this.forceChunk(pos, false);
		}
	}

	@Override
	public boolean containsPos(BlockPos pos) {
		return this.loadedPositions.contains(pos.asLong());
	}

	@Override
	public void tick() {
		for (TickTask<IChunk> process : this.scheduledChunkProcesses) {
			process.tick();
		}
		this.scheduledChunkProcesses.removeIf(TickTask::isComplete);
	}

	private void forceChunk(BlockPos pos, boolean load) {
		if (this.world != null) {
			this.world.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, load);
		}
	}

	public void scheduleChunkProcess(IChunk chunk, Consumer<IChunk> chunkProcess, int ticks) {
		this.scheduledChunkProcesses.add(new TickTask<>(chunk, chunkProcess, ticks));
	}
}