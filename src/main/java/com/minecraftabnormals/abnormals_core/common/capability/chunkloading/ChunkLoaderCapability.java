package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class stores important information about the chunk loading capability.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class ChunkLoaderCapability {
	public static Capability<IChunkLoader> CHUNK_LOAD_CAP = CapabilityManager.get(new CapabilityToken<>() {
	});

	/**
	 * Registers the {@link IChunkLoader} capability.
	 * <p><b>This should not get called outside of the mod!</b></p>
	 *
	 * @param event The event to register it to.
	 */
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IChunkLoader.class);
	}

	/**
	 * The {@link ICapabilityProvider} implementation for the {@link IChunkLoader} capability.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
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