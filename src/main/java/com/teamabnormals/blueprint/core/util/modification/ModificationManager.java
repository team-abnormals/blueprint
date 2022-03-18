package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.Gson;
import com.teamabnormals.blueprint.core.events.SimpleJsonResourceListenerPreparedEvent;
import com.teamabnormals.blueprint.core.util.modification.selection.SelectionSpace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class containing a map of the {@link ResourceLocation} targets and a prioritized {@link EnumMap} of {@link ConfiguredModifier} instances for said target.
 * <p>Use this for simple prioritized management of {@link ConfiguredModifier} instances.</p>
 *
 * @param <T> The type of object to modify.
 * @param <S> The type of additional serialization object.
 * @param <D> The type of additional deserialization object.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 * @see ConfiguredModifier
 */
public abstract class ModificationManager<T, S, D> extends SimpleJsonResourceReloadListener {
	private final Map<ResourceLocation, EnumMap<EventPriority, List<ConfiguredModifier<T, ?, S, D, ?>>>> modifiers = new HashMap<>();
	private SelectionSpace unmodifiedEntries = consumer -> {};

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
	 * Constructor for a {@link ModificationManager} that will auto-refresh its {@link #unmodifiedEntries}.
	 *
	 * @param gson            A {@link Gson} instance to use for deserialization.
	 * @param directory       The directory to read from.
	 * @param targetDirectory The directory to refresh the {@link #unmodifiedEntries} from.
	 */
	public ModificationManager(Gson gson, String directory, String targetDirectory) {
		super(gson, directory);
		MinecraftForge.EVENT_BUS.addListener((SimpleJsonResourceListenerPreparedEvent event) -> {
			if (event.getDirectory().equals(targetDirectory)) {
				var entries = event.getEntries().keySet();
				this.unmodifiedEntries = entries::forEach;
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
		return this.putModifiers(target, EventPriority.NORMAL, configuredModifiers);
	}

	/**
	 * Puts a list of prioritized {@link ConfiguredModifier} instances onto the map for a target {@link ResourceLocation}.
	 *
	 * @param target              The target {@link ResourceLocation}.
	 * @param priority            The {@link EventPriority} to prioritize the modifiers to.
	 * @param configuredModifiers A list of {@link ConfiguredModifier instances to put onto the map.
	 * @return A list of {@link ConfiguredModifier instances that were put onto the map.
	 */
	public List<ConfiguredModifier<T, ?, S, D, ?>> putModifiers(ResourceLocation target, EventPriority priority, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		return this.modifiers.computeIfAbsent(target, key -> new EnumMap<>(EventPriority.class)).put(priority, configuredModifiers);
	}

	/**
	 * Adds a {@link ConfiguredModifier} for a target {@link ResourceLocation}.
	 *
	 * @param target             The target {@link ResourceLocation}.
	 * @param configuredModifier A {@link ConfiguredModifier} to add to the map.
	 * @return If the {@link ConfiguredModifier} was successfully added to the map.
	 */
	public boolean addModifier(ResourceLocation target, ConfiguredModifier<T, ?, S, D, ?> configuredModifier) {
		return this.addModifier(target, EventPriority.NORMAL, configuredModifier);
	}

	/**
	 * Adds a prioritized {@link ConfiguredModifier} for a target {@link ResourceLocation}.
	 *
	 * @param target             The target {@link ResourceLocation}.
	 * @param priority           The {@link EventPriority} to prioritize the modifier to.
	 * @param configuredModifier A {@link ConfiguredModifier} to add to the map.
	 * @return If the {@link ConfiguredModifier} was successfully added to the map.
	 */
	public boolean addModifier(ResourceLocation target, EventPriority priority, ConfiguredModifier<T, ?, S, D, ?> configuredModifier) {
		return this.modifiers.computeIfAbsent(target, key -> new EnumMap<>(EventPriority.class)).computeIfAbsent(priority, location -> new ArrayList<>()).add(configuredModifier);
	}

	/**
	 * Adds a list of {@link ConfiguredModifier} instances for a target {@link ResourceLocation}.
	 *
	 * @param target              The target {@link ResourceLocation}.
	 * @param configuredModifiers A list of {@link ConfiguredModifier} instances to add to the map.
	 * @return If the list of {@link ConfiguredModifier} instances were successfully added to the map.
	 */
	public boolean addModifiers(ResourceLocation target, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		return this.addModifiers(target, EventPriority.NORMAL, configuredModifiers);
	}

	/**
	 * Adds a list of {@link ConfiguredModifier} instances for a target {@link ResourceLocation}.
	 *
	 * @param target              The target {@link ResourceLocation}.
	 * @param priority            The {@link EventPriority} to prioritize the modifiers to.
	 * @param configuredModifiers A list of {@link ConfiguredModifier} instances to add to the map.
	 * @return If the list of {@link ConfiguredModifier} instances were successfully added to the map.
	 */
	public boolean addModifiers(ResourceLocation target, EventPriority priority, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		return this.modifiers.computeIfAbsent(target, key -> new EnumMap<>(EventPriority.class)).computeIfAbsent(priority, location -> new ArrayList<>()).addAll(configuredModifiers);
	}

	/**
	 * Adds a list of {@link ConfiguredModifier} instances for a list of target {@link ResourceLocation} instances.
	 *
	 * @param targets             A list of target {@link ResourceLocation} instances.
	 * @param configuredModifiers A list of {@link ConfiguredModifier} instances to add to the map.
	 * @return If the list of {@link ConfiguredModifier} instances were successfully added to the map.
	 */
	public boolean addModifiers(List<ResourceLocation> targets, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		return this.addModifiers(targets, EventPriority.NORMAL, configuredModifiers);
	}

	/**
	 * Adds a list of {@link ConfiguredModifier} instances for a list of target {@link ResourceLocation} instances.
	 *
	 * @param targets             A list of target {@link ResourceLocation} instances.
	 * @param priority            The {@link EventPriority} to prioritize the modifiers to.
	 * @param configuredModifiers A list of {@link ConfiguredModifier} instances to add to the map.
	 * @return If the list of {@link ConfiguredModifier} instances were successfully added to the map.
	 */
	public boolean addModifiers(List<ResourceLocation> targets, EventPriority priority, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		var modifiers = this.modifiers;
		boolean successful = true;
		for (ResourceLocation target : targets) {
			successful &= modifiers.computeIfAbsent(target, key -> new EnumMap<>(EventPriority.class)).computeIfAbsent(priority, location -> new ArrayList<>()).addAll(configuredModifiers);
		}
		return successful;
	}

	/**
	 * Gets the prioritized {@link ConfiguredModifier} instances for a given target {@link ResourceLocation} and {@link EventPriority}.
	 *
	 * @param resourceLocation The target {@link ResourceLocation}.
	 * @param priority The {@link EventPriority} to get the prioritized modifiers.
	 * @return The prioritized {@link ConfiguredModifier} instances for a given target {@link ResourceLocation}, or null if no modifiers exist for the given target.
	 */
	@Nullable
	public List<ConfiguredModifier<T, ?, S, D, ?>> getModifiers(ResourceLocation resourceLocation, EventPriority priority) {
		var priorityMap = this.modifiers.get(resourceLocation);
		return priorityMap != null ? priorityMap.get(priority) : null;
	}

	/**
	 * Gets the {@link #unmodifiedEntries} used for the resources searching space for target selectors.
	 *
	 * @return The {@link #unmodifiedEntries} used for the resources searching space for target selectors.
	 */
	public SelectionSpace getUnmodifiedEntries() {
		return this.unmodifiedEntries;
	}

	/**
	 * Sets the {@link #unmodifiedEntries} to a new {@link SelectionSpace} instance.
	 *
	 * @param unmodifiedEntries A new {@link SelectionSpace} instance to use.
	 */
	public void setUnmodifiedEntries(SelectionSpace unmodifiedEntries) {
		this.unmodifiedEntries = unmodifiedEntries;
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
