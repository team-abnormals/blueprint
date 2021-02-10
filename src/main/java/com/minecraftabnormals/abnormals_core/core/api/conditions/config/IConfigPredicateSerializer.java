package com.minecraftabnormals.abnormals_core.core.api.conditions.config;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

/**
 * Serializer for an {@link IConfigPredicate}, generating a value from JSON.
 *
 * <p>The allowed arguments depends on the specific predicate, but 'inverted' will always be parsed as meaning "take the opposite of the predicate's return value"</p>
 *
 * @param <T> the {@link IConfigPredicate} type to serialize
 */
public interface IConfigPredicateSerializer<T extends IConfigPredicate> {
    /**
     * Serializes the predicate's arguments to JSON
     *
     * @param json the JSON object to write to
     * @param value the {@link IConfigPredicate} to serialize
     *
     * @throws IllegalArgumentException if {@code value} is not of type {@link T}
     */
    void write(JsonObject json, IConfigPredicate value) throws IllegalArgumentException;

    /**
     * Attempts to deserialize an {@link IConfigPredicate} of type {@link T} from a specified JSON object
     *
     * @param json the JSON object to read from
     * @return the deserialized {@link IConfigPredicate}
     */
    T read(JsonObject json);

    /**
     * Gets the ID that corresponds to this deserializer when reading from JSON (e.g. {@code "abnormals_core:equals"})
     *
     * @return a {@link ResourceLocation} representing the serializer's unique identifier
     */
    ResourceLocation getID();
}
