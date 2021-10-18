package com.teamabnormals.blueprint.common.capability.chunkloading;

import com.google.common.collect.Lists;
import com.teamabnormals.blueprint.core.util.TickTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * An {@link IChunkLoader} implementation that can load and unload Chunks, as well as schedule tick tasks on Chunks.
 *
 * @author SmellyModder(Luke Tonon)
 * @see IChunkLoader
 */
public class ChunkLoader implements IChunkLoader {
	public final List<Long> loadedPositions = Lists.newArrayList();
	@Nullable
	private final ServerLevel level;
	private final List<TickTask<ChunkAccess>> scheduledChunkProcesses = Lists.newArrayList();

	public ChunkLoader(@Nullable ServerLevel level) {
		this.level = level;
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
		for (TickTask<ChunkAccess> process : this.scheduledChunkProcesses) {
			process.tick();
		}
		this.scheduledChunkProcesses.removeIf(TickTask::isComplete);
	}

	private void forceChunk(BlockPos pos, boolean load) {
		ServerLevel level = this.level;
		if (level != null) {
			level.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, load);
		}
	}

	/**
	 * Schedules a process to happen on a chunk after a specified time.
	 *
	 * @param chunk        Access to the chunk to perform the process on.
	 * @param chunkProcess The process to perform.
	 * @param ticks        The delay until the process is performed.
	 */
	public void scheduleChunkProcess(ChunkAccess chunk, Consumer<ChunkAccess> chunkProcess, int ticks) {
		this.scheduledChunkProcesses.add(new TickTask<>(chunk, chunkProcess, ticks));
	}

	@Override
	public Tag serializeNBT() {
		return new LongArrayTag(this.loadedPositions);
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		this.loadedPositions.clear();
		for (Long pos : ((LongArrayTag) nbt).getAsLongArray()) {
			this.addPos(BlockPos.of(pos));
		}
	}
}