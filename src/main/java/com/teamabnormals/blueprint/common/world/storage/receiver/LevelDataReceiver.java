package com.teamabnormals.blueprint.common.world.storage.receiver;

import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class works as an easy way of creating and quickly accessing objects associated with a specific {@link ServerLevel}.
 * These obejcts do not get saved.
 */
public abstract class LevelDataReceiver<T> {
	private static final List<LevelDataReceiver<?>> RECEIVERS = new ArrayList<>();
	private final int key;

	public LevelDataReceiver() {
		this.key = RECEIVERS.size();
		RECEIVERS.add(this);
	}

	public abstract T create(ServerLevel level);

	@SuppressWarnings("unchecked")
	public T get(ServerLevel level) {
		return (T) ((BlueprintServerLevel) level).getLevelData(this.key);
	}

	public static List<LevelDataReceiver<?>> getReceivers() {
		return RECEIVERS;
	}
}