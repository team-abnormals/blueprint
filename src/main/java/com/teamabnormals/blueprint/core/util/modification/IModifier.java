package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * An interface that can be used to modify a type of object with a serializable/deserializable config.
 * <p>This paves the way for the simple creation of data-driven modification systems.</p>
 *
 * @param <T> The type of object that this modifier will modify.
 * @param <C> The type of config object for this modifier.
 * @param <S> The type of additional object to use when serializing the config for this modifier. Make this {@link Void} if not needed.
 * @param <D> The type of additional object to use when deserializing the config for this modifier. Make this {@link Void} if not needed.
 * @author SmellyModder (Luke Tonon)
 * @see ConfiguredModifier
 * @see TargetedModifier
 */
public interface IModifier<T, C, S, D> {
	/**
	 * Creates a new {@link ConfiguredModifier} from this {@link IModifier} with a given config object.
	 *
	 * @param config A config object to store in the {@link ConfiguredModifier}.
	 * @return A new created {@link ConfiguredModifier} containing the given config.
	 */
	default ConfiguredModifier<T, C, S, D, IModifier<T, C, S, D>> withConfiguration(C config) {
		return new ConfiguredModifier<>(this, config);
	}

	/**
	 * Deserializes a new {@link ConfiguredModifier} from this {@link IModifier} from a given {@link JsonElement}.
	 *
	 * @param config     A {@link JsonElement} to deserialize a config from.
	 * @param additional An additional deserialization object.
	 * @return A new deserialized {@link ConfiguredModifier} from this {@link IModifier} from the given {@link JsonElement}.
	 */
	default ConfiguredModifier<T, C, S, D, IModifier<T, C, S, D>> deserializeConfigured(JsonElement config, D additional) {
		return withConfiguration(this.deserialize(config, additional));
	}

	/**
	 * Modifies an object of type {@code <T>} with a given config.
	 *
	 * @param object An object of type {@code <T>} to modify.
	 * @param config A config object of type {@code <C>} to be used in modifying the object of type {@code <T>}.
	 */
	void modify(T object, C config);

	/**
	 * Serializes a {@link JsonElement} from a config object and an additional serialization object.
	 *
	 * @param config     A config object of type {@code <C>}.
	 * @param additional An additional serialization object of type {@code <S>}.
	 * @return A new serialized {@link JsonElement} from the config object and additional serialization object.
	 * @throws JsonParseException If an error occurs when serializing the {@link JsonElement}.
	 */
	JsonElement serialize(C config, S additional) throws JsonParseException;

	/**
	 * Deserializes a config object of type {@code <C>} from a {@link JsonElement} and an additional context object of type {@code <D>}.
	 *
	 * @param element    A {@link JsonElement} to deserialize a new config object from.
	 * @param additional An additional context object of type {@code <D>} to use when deserializing the new config object.
	 * @return A new deserialized config object of type {@code <C>}.
	 * @throws JsonParseException If an error occurs when deserializing a config object.
	 */
	C deserialize(JsonElement element, D additional) throws JsonParseException;
}
