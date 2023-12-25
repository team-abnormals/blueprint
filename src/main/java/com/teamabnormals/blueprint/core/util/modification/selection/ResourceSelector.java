package com.teamabnormals.blueprint.core.util.modification.selection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The interface that represents a serializable resource selector.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface ResourceSelector<S extends ResourceSelector<S>> {
	Codec<ResourceSelector<?>> CODEC = ExtraCodecs.JSON.flatXmap(element -> {
		if (element instanceof JsonPrimitive primitive && primitive.isString()) {
			return DataResult.success(new NamesResourceSelector(new ResourceLocation(primitive.getAsString())));
		} else if (element instanceof JsonObject jsonObject) {
			String type = GsonHelper.getAsString(jsonObject, "type");
			ResourceSelector.Serializer<?> serializer = ResourceSelectorSerializers.INSTANCE.getSerializer(type);
			if (serializer == null) return DataResult.error(() -> "Unknown selector type: " + type);
			try {
				return DataResult.success(serializer.deserialize(jsonObject.get("config")));
			} catch (JsonParseException exception) {
				return DataResult.error(exception::getMessage);
			}
		} else if (element == null) return DataResult.error(() -> "Missing 'selector' member!");
		return DataResult.error(() -> "'selector' must be a string or object!");
	}, selector -> {
		if (selector instanceof NamesResourceSelector namesResourceSelector) {
			var names = namesResourceSelector.names();
			if (names.size() == 1) return DataResult.success(new JsonPrimitive(names.iterator().next().toString()));
		}
		ResourceSelector.Serializer<?> serializer = selector.getSerializer();
		String type = ResourceSelectorSerializers.INSTANCE.getSerializerID(serializer);
		if (type == null) return DataResult.error(() -> "Could not find name for selector serializer: " + serializer);
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("type", new JsonPrimitive(type));
		jsonObject.add("config", selector.serialize());
		return DataResult.success(jsonObject);
	});

	static Predicate<ResourceLocation> predicate(@Nullable ResourceSelector<?> selector) {
		return selector == null ? location -> true : selector.select().map(set -> set::contains, predicate -> predicate);
	}

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
