package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.events.SimpleJsonResourceListenerPreparedEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A class containing a map of the {@link ResourceLocation} targets and a list of {@link ConfiguredModifier} instances for said target.
 * <p>Use this for simple management of {@link ConfiguredModifier} instances.</p>
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
	private Set<Map.Entry<ResourceLocation, JsonElement>> unmodifiedEntries = new HashSet<>();

	/**
	 * Constructor for a {@link ModificationManager} that will NOT refresh its {@link #unmodifiedEntries}.
	 *
	 * @param gson      A {@link Gson} instance to use for deserialization.
	 * @param directory The directory to read from.
	 */
	public ModificationManager(Gson gson, String directory) {
		super(gson, directory);
	}

	/**
	 * Constructor for a {@link ModificationManager} that will refresh its {@link #unmodifiedEntries}.
	 *
	 * @param gson            A {@link Gson} instance to use for deserialization.
	 * @param directory       The directory to read from.
	 * @param targetDirectory The directory to refresh the {@link #unmodifiedEntries} from.
	 */
	public ModificationManager(Gson gson, String directory, String targetDirectory) {
		super(gson, directory);
		MinecraftForge.EVENT_BUS.addListener((SimpleJsonResourceListenerPreparedEvent event) -> {
			if (event.getDirectory().equals(targetDirectory)) {
				this.unmodifiedEntries = event.getEntries().entrySet();
			}
		});
	}

	/**
	 * Puts a list of {@link ConfiguredModifier} instances onto the map for a target {@link ResourceLocation}.
	 *
	 * @param target              The target {@link ResourceLocation}.
	 * @param configuredModifiers A list of {@link ConfiguredModifier instances to put onto the map.
	 * @return A list of {@link ConfiguredModifier instances that were put onto the map.
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
	 * Adds a list of {@link ConfiguredModifier} instances for a target {@link ResourceLocation}.
	 *
	 * @param target              The target {@link ResourceLocation}.
	 * @param configuredModifiers A list of {@link ConfiguredModifier} instances to add to the map.
	 * @return If the list of {@link ConfiguredModifier} instances were successfully added to the map.
	 */
	public boolean addModifiers(ResourceLocation target, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		return this.modifiers.computeIfAbsent(target, location -> new ArrayList<>()).addAll(configuredModifiers);
	}

	/**
	 * Adds a list of {@link ConfiguredModifier} instances for a list of target {@link ResourceLocation} instances.
	 *
	 * @param targets             A list of target {@link ResourceLocation} instances.
	 * @param configuredModifiers A list of {@link ConfiguredModifier} instances to add to the map.
	 * @return If the list of {@link ConfiguredModifier} instances were successfully added to the map.
	 */
	public boolean addModifiers(List<ResourceLocation> targets, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		Map<ResourceLocation, List<ConfiguredModifier<T, ?, S, D, ?>>> modifiers = this.modifiers;
		boolean successful = true;
		for (ResourceLocation target : targets) {
			successful &= modifiers.computeIfAbsent(target, location -> new ArrayList<>()).addAll(configuredModifiers);
		}
		return successful;
	}

	/**
	 * Gets the {@link ConfiguredModifier} instances for a given target {@link ResourceLocation}.
	 *
	 * @param resourceLocation The target {@link ResourceLocation}.
	 * @return The {@link ConfiguredModifier} instances for a given target {@link ResourceLocation}, or null if no modifiers exist for the given target.
	 */
	@Nullable
	public List<ConfiguredModifier<T, ?, S, D, ?>> getModifiers(ResourceLocation resourceLocation) {
		return this.modifiers.get(resourceLocation);
	}

	/**
	 * Gets the {@link #unmodifiedEntries} used for the resources searching space for target selectors.
	 *
	 * @return The {@link #unmodifiedEntries} used for the resources searching space for target selectors.
	 */
	public Set<Map.Entry<ResourceLocation, JsonElement>> getUnmodifiedEntries() {
		return this.unmodifiedEntries;
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
