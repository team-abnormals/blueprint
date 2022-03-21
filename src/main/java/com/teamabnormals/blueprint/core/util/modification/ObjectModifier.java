package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * A serializable interface that can be used to modify a type of object.
 * <p>This paves the way for the simple creation of data-driven modification systems.</p>
 *
 * @param <T> The type of object that this modifier will modify.
 * @param <S> The type of additional object to use when serializing this modifier. Make this {@link Void} if not needed.
 * @param <D> The type of additional object to use when deserializing this modifier. Make this {@link Void} if not needed.
 * @param <M> The type of {@link ObjectModifier} that the {@link #getSerializer()} will use. This should always be the type of the object implementing this interface.
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifierGroup
 * @see Serializer
 */
public interface ObjectModifier<T, S, D, M extends ObjectModifier<T, S, D, M>> {
	/**
	 * Serializes this modifier into a {@link JsonElement} instance.
	 *
	 * @param additional An additional object of type {@code <S>} to assist in serialization.
	 * @return A {@link JsonElement} instance representing the modifier.
	 * @throws JsonParseException If an error occurs when serializing.
	 */
	@SuppressWarnings("unchecked")
	default JsonElement serialize(S additional) throws JsonParseException {
		return this.getSerializer().serialize((M) this, additional);
	}

	/**
	 * Modifies an object of type {@code <T>}.
	 *
	 * @param object An object of type {@code <T>} to modify.
	 */
	void modify(T object);

	/**
	 * Gets the {@link Serializer} instance used for serializing and deserializing this modifier.
	 *
	 * @return The {@link Serializer} instance used for serializing and deserializing this modifier.
	 */
	Serializer<M, S, D> getSerializer();

	/**
	 * The interface for describing how to serialize and deserialize instances of a type of {@link ObjectModifier}.
	 *
	 * @param <M> The type of {@link ObjectModifier} instances to serialize and deserialize.
	 * @param <S> The type of additional object to use when serializing.
	 * @param <D> The type of additional object to use when deserializing.
	 * @author SmellyModder (Luke Tonon)
	 * @see ObjectModifier
	 */
	interface Serializer<M extends ObjectModifier<?, S, D, M>, S, D> {
		/**
		 * Serializes a {@link ObjectModifier} instance of type {@code <M>} into a {@link JsonElement} instance.
		 *
		 * @param additional An additional object of type {@code <S>} to assist in serialization.
		 * @return A {@link JsonElement} instance representing the modifier.
		 * @throws JsonParseException If an error occurs when serializing.
		 */
		JsonElement serialize(M modifier, S additional) throws JsonParseException;

		/**
		 * Deserializes a {@link ObjectModifier} instance of type {@code <M>} from a {@link JsonElement} instance.
		 *
		 * @param element    A {@link JsonElement} to deserialize a new {@link ObjectModifier} instance from.
		 * @param additional An additional object of type {@code <D>} to assist in deserialization.
		 * @return A new {@link ObjectModifier} instance of type {@code <M>}.
		 * @throws JsonParseException If an error occurs when deserializing.
		 */
		M deserialize(JsonElement element, D additional) throws JsonParseException;
	}
}
