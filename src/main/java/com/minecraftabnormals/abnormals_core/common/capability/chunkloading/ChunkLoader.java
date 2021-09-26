package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import com.google.common.collect.Lists;
import com.minecraftabnormals.abnormals_core.core.util.TickTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Can load and unload Chunks, as well as schedule tick tasks on Chunks
 *
 * @author SmellyModder(Luke Tonon)
 */
public class ChunkLoader implements IChunkLoader {
	@Nullable
	private final ServerLevel world;
	public final List<Long> loadedPositions = Lists.newArrayList();
	private final List<TickTask<ChunkAccess>> scheduledChunkProcesses = Lists.newArrayList();

	public ChunkLoader(@Nullable ServerLevel world) {
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
		for (TickTask<ChunkAccess> process : this.scheduledChunkProcesses) {
			process.tick();
		}
		this.scheduledChunkProcesses.removeIf(TickTask::isComplete);
	}

	private void forceChunk(BlockPos pos, boolean load) {
		if (this.world != null) {
			this.world.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, load);
		}
	}

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