package com.teamabnormals.blueprint.common.world.storage.receiver;

import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for creating and quickly accessing a level specific {@link ConcurrentHashMap}.
 */
public class LevelConcurrentHashMapReceiver<K, V> extends LevelDataReceiver<ConcurrentHashMap<K, V>> {

	@Override
	public ConcurrentHashMap<K, V> create(ServerLevel level) {
		return new ConcurrentHashMap<>();
	}
}