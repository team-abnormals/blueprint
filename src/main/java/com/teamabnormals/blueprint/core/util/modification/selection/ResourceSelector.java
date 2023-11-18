package com.teamabnormals.blueprint.core.util.modification.selection;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.function.Predicate;

/**
 * The interface that represents a serializable resource selector.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface ResourceSelector<S extends ResourceSelector<S>> {
	/**
	 * Serializes this {@link ResourceSelector} instance.
	 *
	 * @return A {@link JsonElement} instance representing this {@link ResourceSelector} instance.
	 * @see Serializer#serialize(ResourceSelector)
	 */
	@SuppressWarnings("unchecked")
	default JsonElement serialize() {
		return this.getSerializer().serialize((S) this);
	}

	Either<Set<ResourceLocation>, Predicate<ResourceLocation>> select();

	/**
	 * Gets the {@link Serializer} instance used for this {@link ResourceSelector} instance.
	 *
	 * @return The {@link Serializer} instance used for this {@link ResourceSelector} instance.
	 */
	Serializer<S> getSerializer();

	/**
	 * The interface that represents a serializer and deserializer for a type of {@link ResourceSelector}.
	 *
	 * @param <S> The type of {@link ResourceSelector} instances to serialize and deserialize.
	 * @author SmellyModder (Luke Tonon)
	 */
	interface Serializer<S extends ResourceSelector<?>> {
		/**
		 * Serializes a {@link ResourceSelector} instance.
		 *
		 * @param selector A {@link ResourceSelector} instance to serialize.
		 * @return A {@link JsonElement} instance representing the given {@link ResourceSelector} instance.
		 */
		JsonElement serialize(S selector);

		/**
		 * Deserializes a new {@link ResourceSelector} instance from a {@link JsonElement} instance.
		 *
		 * @param element A {@link JsonElement} instance representing a {@link ResourceSelector} instance.
		 * @return A new {@link ResourceSelector} instance deserialized from a {@link JsonElement} instance.
		 */
		S deserialize(JsonElement element);
	}
}
