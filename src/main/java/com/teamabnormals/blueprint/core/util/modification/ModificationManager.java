package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.Gson;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class containing a map of the {@link ResourceLocation} targets and a list of {@link ConfiguredModifier}s for said target.
 * <p>Use this for simple management of {@link ConfiguredModifier}s.</p>
 *
 * @param <T> The type of object to modify.
 * @param <S> The type of additional serialization object.
 * @param <D> The type of additional deserialization object.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 * @see ConfiguredModifier
 */
public abstract class ModificationManager<T, S, D> extends SimpleJsonResourceReloadListener {
	private final Map<ResourceLocation, List<ConfiguredModifier<T, ?, S, D, ?>>> modifiers = new HashMap<>();

	public ModificationManager(Gson gson, String string) {
		super(gson, string);
	}

	/**
	 * Puts a list of {@link ConfiguredModifier}s onto the map for a target {@link ResourceLocation}.
	 *
	 * @param target              The target {@link ResourceLocation}.
	 * @param configuredModifiers A list of {@link ConfiguredModifier}s to put onto the map.
	 * @return A list of {@link ConfiguredModifier}s that were put onto the map.
	 */
	public List<ConfiguredModifier<T, ?, S, D, ?>> putModifiers(ResourceLocation target, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		return this.modifiers.put(target, configuredModifiers);
	}

	/**
	 * Adds a {@link ConfiguredModifier} for a target {@link ResourceLocation}.
	 *
	 * @param target             The target {@link ResourceLocation}.
	 * @param configuredModifier A {@link ConfiguredModifier} to add to the map.
	 * @return If the {@link ConfiguredModifier} was successfully added to the map.
	 */
	public boolean addModifier(ResourceLocation target, ConfiguredModifier<T, ?, S, D, ?> configuredModifier) {
		return this.modifiers.computeIfAbsent(target, location -> new ArrayList<>()).add(configuredModifier);
	}

	/**
	 * Adds a list of {@link ConfiguredModifier}s for a target {@link ResourceLocation}.
	 *
	 * @param target              The target {@link ResourceLocation}.
	 * @param configuredModifiers A list of {@link ConfiguredModifier}s to add to the map.
	 * @return If the list of {@link ConfiguredModifier}s were successfully added to the map.
	 */
	public boolean addModifiers(ResourceLocation target, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		return this.modifiers.computeIfAbsent(target, location -> new ArrayList<>()).addAll(configuredModifiers);
	}

	/**
	 * Gets the {@link ConfiguredModifier}s for a given target {@link ResourceLocation}.
	 *
	 * @param resourceLocation The target {@link ResourceLocation}.
	 * @return The {@link ConfiguredModifier}s for a given target {@link ResourceLocation}, or null if no modifiers exist for the given target.
	 */
	@Nullable
	public List<ConfiguredModifier<T, ?, S, D, ?>> getModifiers(ResourceLocation resourceLocation) {
		return this.modifiers.get(resourceLocation);
	}

	/**
	 * Gets the size of the internal map.
	 *
	 * @return The size of the internal map.
	 */
	public int size() {
		return this.modifiers.size();
	}

	/**
	 * Resets the manager, clearing the map.
	 */
	public void reset() {
		this.modifiers.clear();
	}
}
