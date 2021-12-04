package com.teamabnormals.blueprint.core.util.modification.targeting;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * The interface that represents a serializable selector for modifier targets.
 *
 * @param <C> The type of config object.
 * @author SmellyModder (Luke Tonon)
 */
public interface ModifierTargetSelector<C> {
	/**
	 * Creates a new {@link ConfiguredModifierTargetSelector} containing this target selector and a given config object.
	 *
	 * @param config A config object of type {@code <C>}.
	 * @return A new {@link ConfiguredModifierTargetSelector} containing this target selector and the given config object.
	 */
	default ConfiguredModifierTargetSelector<C, ModifierTargetSelector<C>> withConfiguration(C config) {
		return new ConfiguredModifierTargetSelector<>(this, config);
	}

	/**
	 * Deserializes a new {@link ConfiguredModifierTargetSelector} from a given JSON config.
	 *
	 * @param config A JSON config to deserialize.
	 * @return A newly deserialized {@link ConfiguredModifierTargetSelector} from the given JSON config.
	 * @throws JsonParseException If a deserialization error occurs.
	 */
	default ConfiguredModifierTargetSelector<C, ModifierTargetSelector<C>> deserializeConfigured(JsonElement config) throws JsonParseException {
		return this.withConfiguration(this.deserialize(config));
	}

	/**
	 * Gets a list of names to use for targeting objects.
	 *
	 * @param space  A {@link SelectionSpace} instance containing paths and their respective JSON elements.
	 * @param config A config object of type {@code <C>} to use when calculating the list of names.
	 * @return A list of names to use for targeting objects.
	 */
	List<ResourceLocation> getTargetNames(SelectionSpace space, C config);

	/**
	 * Serializes a given config object of type {@code <C>}.
	 *
	 * @param config A config object of type {@code <C>}.
	 * @return A serialized config object of type {@code <C>} in the form of a {@link JsonElement}.
	 */
	JsonElement serialize(C config);

	/**
	 * Deserializes a new config object of type {@code <C>}.
	 *
	 * @param element A {@link JsonElement} to deserialize.
	 * @return A newly deserialized config object of type {@code <C>}.
	 * @throws JsonParseException If a deserialization error occurs.
	 */
	C deserialize(JsonElement element) throws JsonParseException;
}
