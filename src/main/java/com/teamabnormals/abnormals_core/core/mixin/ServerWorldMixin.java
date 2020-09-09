package com.teamabnormals.abnormals_core.core.mixin;

import com.teamabnormals.abnormals_core.common.world.storage.GlobalStorageManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author SmellyModder (Luke Tonon)
 */
@Mixin(ServerWorld.class)
public final class ServerWorldMixin {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(MinecraftServer server, Executor workerExecutor, SaveFormat.LevelSave levelSave, IServerWorldInfo serverWorldInfo, RegistryKey<World> registryKey, DimensionType dimensionType, IChunkStatusListener statusListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<ISpecialSpawner> list, boolean bl2, CallbackInfo info) {
		//Overworld
		if (registryKey == World.field_234918_g_) {
			GlobalStorageManager.getOrCreate((ServerWorld) (Object) this);
		}
	}

}
