package com.teamabnormals.blueprint.core.util.modification;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;

/**
 * A registry class for {@link IModifier}s.
 * <p>Create a new instance to make a registry for {@link IModifier}s that will modify a type of object.</p>
 *
 * @param <T> The type of object the {@link IModifier}s in this registry will modify.
 * @param <S> The type of the additional serialization object the {@link IModifier}s in this registry will use.
 * @param <D> The type of the additional deserialization object the {@link IModifier}s in this registry will use.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 */
public class ModifierRegistry<T, S, D> {
	private final BiMap<String, IModifier<T, ?, S, D>> modifiers = HashBiMap.create();

	/**
	 * Registers a {@link IModifier} for a given name. This method is safe to call during parallel mod-loading.
	 * <p>The name should be prefixed with your mod's ID.</p>
	 *
	 * @param name     The name to register the {@link IModifier} by.
	 * @param modifier The {@link IModifier} to register.
	 * @param <C>      The type of the config object for the {@link IModifier}.
	 * @param <M>      The type of the {@link IModifier}.
	 * @return The given {@link IModifier}.
	 * @throws IllegalArgumentException If a {@link IModifier} is already registered with the given name.
	 */
	public synchronized <C, M extends IModifier<T, C, S, D>> M register(String name, M modifier) {
		if (this.modifiers.containsKey(name)) {
			throw new IllegalArgumentException("A modifier with name '" + name + "' is already registered!");
		}
		this.modifiers.put(name, modifier);
		return modifier;
	}

	/**
	 * Gets a {@link IModifier} by its name in the registry.
	 *
	 * @param name The name of the {@link IModifier} to lookup.
	 * @return A {@link IModifier} looked up by its name, or null if no such {@link IModifier} with the specified name could be found.
	 */
	@Nullable
	public IModifier<T, ?, S, D> getModifier(String name) {
		return this.modifiers.get(name);
	}

	/**
	 * Gets a name for a given {@link IModifier}.
	 *
	 * @param modifier A {@link IModifier} to lookup.
	 * @return The name for the given {@link IModifier}, or null if the modifier isn't in the registry.
	 */
	@Nullable
	public String getName(IModifier<T, ?, S, D> modifier) {
		return this.modifiers.inverse().get(modifier);
	}
}
