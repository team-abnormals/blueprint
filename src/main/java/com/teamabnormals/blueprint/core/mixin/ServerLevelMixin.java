package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.storage.GlobalStorageManager;
import com.teamabnormals.blueprint.common.world.storage.receiver.LevelDataReceiver;
import com.teamabnormals.blueprint.common.world.storage.receiver.BlueprintServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author SmellyModder (Luke Tonon)
 */
@Mixin(ServerLevel.class)
public final class ServerLevelMixin implements BlueprintServerLevel {
	private Object[] levelData;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess levelSave, ServerLevelData serverWorldInfo, ResourceKey<Level> resourceKey, LevelStem levelStem, ChunkProgressListener statusListener, boolean bl, long l, List<CustomSpawner> list, boolean bl2, @Nullable RandomSequences randomSequences, CallbackInfo info) {
		if (resourceKey == Level.OVERWORLD) {
			GlobalStorageManager.getOrCreate((ServerLevel) (Object) this);
		}

		List<LevelDataReceiver<?>> receivers = LevelDataReceiver.getReceivers();
		this.levelData = new Object[receivers.size()];
		for (int i = 0; i < receivers.size(); i++) {
			this.levelData[i] = receivers.get(i).create((ServerLevel) (Object) this);
		}
	}

	@Override
	public Object getLevelData(int index) {
		return this.levelData[index];
	}
}