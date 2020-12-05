package com.minecraftabnormals.abnormals_core.common.capability.chunkloading;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
	public void attachChunkLoaderCap(AttachCapabilitiesEvent<World> event) {
		World world = event.getObject();
		if (!world.isRemote) {
			LazyOptional<IChunkLoader> loaderInstance = LazyOptional.of(() -> new ChunkLoader((ServerWorld) world));
			event.addCapability(new ResourceLocation(AbnormalsCore.MODID, "chunk_loader"), new ChunkLoaderCapability(loaderInstance));
			event.addListener(() -> loaderInstance.invalidate());
		}
	}

	@SubscribeEvent
	public void tickChunkLoader(WorldTickEvent event) {
		World world = event.world;
		if (!world.isRemote && event.phase == Phase.START) {
			world.getCapability(ChunkLoaderCapability.CHUNK_LOAD_CAP).ifPresent(loader -> {
				loader.tick();
			});
		}
	}

}