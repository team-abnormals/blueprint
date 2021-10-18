package com.teamabnormals.blueprint.core.api.conditions.config;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/**
 * Serializer for an {@link IConfigPredicate}, generating a value from JSON or writing to json from a value.
 *
 * <p>The allowed arguments depends on the specific predicate, but the 'inverted' argument is handled outside of its
 * serializer and will always be parsed as meaning "take the opposite of the predicate's return value".</p>
 *
 * @param <T> The {@link IConfigPredicate} type to serialize.
 * @author abigailfails
 */
public interface IConfigPredicateSerializer<T extends IConfigPredicate> {
	/**
	 * Serializes the predicate's arguments to JSON.
	 *
	 * @param json  The JSON object to write to.
	 * @param value The {@link IConfigPredicate} to serialize.
	 * @throws IllegalArgumentException If {@code value} is not of type {@link T}.
	 */
	void write(JsonObject json, IConfigPredicate value) throws IllegalArgumentException;

	/**
	 * Attempts to deserialize an {@link IConfigPredicate} of type {@link T} from a specified JSON object.
	 *
	 * @param json The JSON object to read from.
	 * @return The deserialized {@link IConfigPredicate}.
	 */
	T read(JsonObject json);

	/**
	 * Gets the ID that corresponds to this deserializer when reading from JSON (e.g. {@code "blueprint:equals"}).
	 *
	 * @return A {@link ResourceLocation} representing the serializer's unique identifier.
	 */
	ResourceLocation getID();
}
