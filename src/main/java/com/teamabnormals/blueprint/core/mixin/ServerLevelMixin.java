package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.storage.GlobalStorageManager;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author SmellyModder (Luke Tonon)
 */
@Mixin(ServerLevel.class)
public final class ServerLevelMixin {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess levelSave, ServerLevelData serverWorldInfo, ResourceKey<Level> resourceKey, Holder<DimensionType> dimensionType, ChunkProgressListener statusListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<CustomSpawner> list, boolean bl2, CallbackInfo info) {
		if (resourceKey == Level.OVERWORLD) {
			GlobalStorageManager.getOrCreate((ServerLevel) (Object) this);
		}
	}

}
