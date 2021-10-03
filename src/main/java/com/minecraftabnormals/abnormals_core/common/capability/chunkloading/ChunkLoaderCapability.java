package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class ChunkLoaderCapability {
	public static Capability<IChunkLoader> CHUNK_LOAD_CAP = CapabilityManager.get(new CapabilityToken<>(){});

	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IChunkLoader.class);
	}

	public static class Provider implements ICapabilityProvider {
		private final LazyOptional<IChunkLoader> lazyOptional;

		public Provider(ServerLevel level) {
			this.lazyOptional = LazyOptional.of(() -> new ChunkLoader(level));
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return CHUNK_LOAD_CAP.orEmpty(cap, this.lazyOptional);
		}
	}
}