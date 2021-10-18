package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * A wrapper around a {@link IModifier} that uses its {@link IModifier#modify(Object, Object)} method with a constant config.
 * <p>This works as a way to store a configuration for a {@link IModifier}.</p>
 *
 * @param <T> The type of object that this modifier will modify.
 * @param <C> The type of config object.
 * @param <S> The type of additional serialization object.
 * @param <D> The type of additional deserialization object.
 * @param <M> The type of modifier.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 * @see TargetedModifier
 */
public final class ConfiguredModifier<T, C, S, D, M extends IModifier<T, C, S, D>> {
	private final M modifer;
	private final C config;

	public ConfiguredModifier(M modifer, C config) {
		this.modifer = modifer;
		this.config = config;
	}

	/**
	 * Modifies an object of type {@code <T>}, using the internal config.
	 *
	 * @param object An object of type {@code <T>} to modify.
	 */
	public void modify(T object) {
		this.modifer.modify(object, this.config);
	}

	/**
	 * Serializes a {@link JsonElement} from the internal config object and an additional serialization object.
	 *
	 * @param additional An additional serialization object of type {@code <S>}.
	 * @return A new serialized {@link JsonElement} from the config object and additional serialization object.
	 * @throws JsonParseException If an error occurs when serializing the {@link JsonElement}.
	 */
	public JsonElement serialize(S additional) throws JsonParseException {
		return this.modifer.serialize(this.config, additional);
	}

	/**
	 * Gets the {@link IModifier} that this {@link ConfiguredModifier} contains.
	 *
	 * @return The {@link IModifier} that this {@link ConfiguredModifier} contains.
	 */
	public M getModifer() {
		return this.modifer;
	}

	/**
	 * Gets the config object that this {@link ConfiguredModifier} contains.
	 *
	 * @return The config object that this {@link ConfiguredModifier} contains.
	 */
	public C getConfig() {
		return this.config;
	}
}
