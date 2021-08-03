package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class ChunkLoaderCapability implements ICapabilitySerializable<INBT> {
	@CapabilityInject(IChunkLoader.class)
	public static Capability<IChunkLoader> CHUNK_LOAD_CAP = null;

	private final LazyOptional<IChunkLoader> instance;

	public ChunkLoaderCapability(LazyOptional<IChunkLoader> instance) {
		this.instance = instance;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return CHUNK_LOAD_CAP.orEmpty(cap, this.instance);
	}

	@Override
	public INBT serializeNBT() {
		return CHUNK_LOAD_CAP.writeNBT(this.instance.orElse(null), null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		CHUNK_LOAD_CAP.readNBT(this.instance.orElse(null), null, nbt);
	}

	public static void register() {
		CapabilityManager.INSTANCE.register(IChunkLoader.class, new IStorage<IChunkLoader>() {
			@Override
			public INBT writeNBT(Capability<IChunkLoader> capability, IChunkLoader instance, Direction side) {
				ChunkLoader loader = (ChunkLoader) instance;
				return new LongArrayNBT(loader.loadedPositions);
			}

			@Override
			public void readNBT(Capability<IChunkLoader> capability, IChunkLoader instance, Direction side, INBT nbt) {
				ChunkLoader loader = (ChunkLoader) instance;
				loader.loadedPositions.clear();
				for (Long pos : ((LongArrayNBT) nbt).getAsLongArray()) {
					loader.addPos(BlockPos.of(pos));
				}
			}
		}, () -> new ChunkLoader(null));
	}
}