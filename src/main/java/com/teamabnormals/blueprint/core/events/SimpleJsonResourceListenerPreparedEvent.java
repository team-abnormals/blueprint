package com.teamabnormals.blueprint.core.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

/**
 * An event that gets fired when a {@link net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener} instance finishes preparing its entries.
 *
 * @author SmellyModder (Luke Tonon)
 * @see net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 */
public final class SimpleJsonResourceListenerPreparedEvent extends Event {
	private final Gson gson;
	private final String directory;
	private final Map<ResourceLocation, JsonElement> entries;

	public SimpleJsonResourceListenerPreparedEvent(Gson gson, String directory, Map<ResourceLocation, JsonElement> entries) {
		this.gson = gson;
		this.directory = directory;
		this.entries = entries;
	}

	/**
	 * Gets the {@link Gson} responsible for the listener's reading of JSON files.
	 *
	 * @return The {@link Gson} responsible for the listener's reading of JSON files.
	 */
	public Gson getGson() {
		return this.gson;
	}

	/**
	 * Gets the directory the listener reads from.
	 *
	 * @return The directory the listener reads from.
	 */
	public String getDirectory() {
		return this.directory;
	}

	/**
	 * Gets the prepared map of entries.
	 *
	 * @return The prepared map of entries.
	 */
	public Map<ResourceLocation, JsonElement> getEntries() {
		return this.entries;
	}
}
