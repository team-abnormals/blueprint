package com.teamabnormals.blueprint.core.endimator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * The global registry for all {@link PlayableEndimation} instances.
 * <p>This provides the necessary storage for reading/writing and syncing {@link PlayableEndimation} instances.</p>
 * <p>All {@link PlayableEndimation} instances should get registered here during mod loading.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum PlayableEndimationManager {
	INSTANCE;

	private final BiMap<ResourceLocation, PlayableEndimation> registry = HashBiMap.create();
	private final ObjectList<PlayableEndimation> byID = new ObjectArrayList<>(256);
	private final Object2IntMap<PlayableEndimation> toID = new Object2IntOpenHashMap<>();
	private int nextID;

	PlayableEndimationManager() {
		this.toID.defaultReturnValue(-1);
		this.registerPlayableEndimation(PlayableEndimation.BLANK);
	}

	/**
	 * Registers a given {@link PlayableEndimation}.
	 *
	 * @param playableEndimation A {@link PlayableEndimation} to register.
	 * @return The given {@link PlayableEndimation}.
	 */
	public PlayableEndimation registerPlayableEndimation(PlayableEndimation playableEndimation) {
		return this.registerPlayableEndimation(playableEndimation.location(), playableEndimation);
	}

	/**
	 * Registers a given {@link PlayableEndimation} for a given {@link ResourceLocation} key.
	 *
	 * @param key                A {@link ResourceLocation} to use as the key.
	 * @param playableEndimation A {@link PlayableEndimation} to register.
	 * @return The given {@link PlayableEndimation}.
	 */
	public synchronized PlayableEndimation registerPlayableEndimation(ResourceLocation key, PlayableEndimation playableEndimation) {
		BiMap<ResourceLocation, PlayableEndimation> registry = this.registry;
		if (registry.containsKey(key)) {
			throw new IllegalArgumentException("Duplicate key for Playable Endimation: " + key);
		} else {
			registry.put(key, playableEndimation);
		}
		int nextID = this.nextID;
		this.byID.size(Math.max(this.byID.size(), nextID + 1));
		this.byID.set(nextID, playableEndimation);
		this.toID.put(playableEndimation, nextID);
		this.nextID++;
		return playableEndimation;
	}

	/**
	 * Gets a {@link PlayableEndimation} by its {@link ResourceLocation} key.
	 *
	 * @param key A {@link ResourceLocation} key to look up.
	 * @return A {@link PlayableEndimation} by its {@link ResourceLocation} key, or null if no {@link PlayableEndimation} exists for the key.
	 */
	@Nullable
	public PlayableEndimation getEndimation(ResourceLocation key) {
		return this.registry.get(key);
	}

	/**
	 * Gets the {@link ResourceLocation} key of a given {@link PlayableEndimation}.
	 *
	 * @param playableEndimation A {@link PlayableEndimation} to look up.
	 * @return The {@link ResourceLocation} key of a given {@link PlayableEndimation}, or null if no key exists for the {@link PlayableEndimation}.
	 */
	@Nullable
	public ResourceLocation getKey(PlayableEndimation playableEndimation) {
		return this.registry.inverse().get(playableEndimation);
	}

	/**
	 * Gets a {@link PlayableEndimation} by its ID.
	 *
	 * @param id An ID to look up.
	 * @return A {@link PlayableEndimation} by its ID, or null if no {@link PlayableEndimation} exists for the given ID.
	 */
	@Nullable
	public PlayableEndimation getEndimation(int id) {
		return this.byID.get(id);
	}

	/**
	 * Gets the ID of a given {@link PlayableEndimation}.
	 *
	 * @param endimation A {@link PlayableEndimation} to look up.
	 * @return The ID of a given {@link PlayableEndimation}, or -1 if no ID for the given {@link PlayableEndimation} exists.
	 */
	public int getID(PlayableEndimation endimation) {
		return this.toID.getInt(endimation);
	}
}
