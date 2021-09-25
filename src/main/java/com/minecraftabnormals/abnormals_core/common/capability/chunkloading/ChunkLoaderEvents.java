package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class ChunkLoaderEvents {

	@SubscribeEvent
	public void attachChunkLoaderCap(AttachCapabilitiesEvent<Level> event) {
		Level world = event.getObject();
		if (!world.isClientSide) {
			LazyOptional<IChunkLoader> loaderInstance = LazyOptional.of(() -> new ChunkLoader((ServerLevel) world));
			event.addCapability(new ResourceLocation(AbnormalsCore.MODID, "chunk_loader"), new ChunkLoaderCapability(loaderInstance));
			event.addListener(() -> loaderInstance.invalidate());
		}
	}

	@SubscribeEvent
	public void tickChunkLoader(WorldTickEvent event) {
		Level world = event.world;
		if (!world.isClientSide && event.phase == Phase.START) {
			world.getCapability(ChunkLoaderCapability.CHUNK_LOAD_CAP).ifPresent(loader -> {
				loader.tick();
			});
		}
	}

}