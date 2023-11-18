package com.teamabnormals.blueprint.core.util.modification.selection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.EmptyResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.Set;
import java.util.function.Predicate;

/**
 * The class that represents a {@link ResourceSelector} with conditions.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ConditionedResourceSelector {
	public static final Codec<ConditionedResourceSelector> DIRECT_CODEC = ExtraCodecs.JSON.flatXmap(element -> {
		try {
			return DataResult.success(deserialize("selector", element));
		} catch (JsonParseException exception) {
			String message = exception.getMessage();
			return DataResult.error(() -> message);
		}
	}, selector -> {
		try {
			return DataResult.success(selector.serialize());
		} catch (JsonParseException exception) {
			String message = exception.getMessage();
			return DataResult.error(() -> message);
		}
	});
	public static final Codec<ConditionedResourceSelector> CODEC = DIRECT_CODEC.fieldOf("selector").codec();
	public static final ConditionedResourceSelector EMPTY = new ConditionedResourceSelector(EmptyResourceSelector.INSTANCE);
	private static final ICondition[] NO_CONDITIONS = new ICondition[0];
	private final ResourceSelector<?> resourceSelector;
	private final ICondition[] conditions;

	public ConditionedResourceSelector(ResourceSelector<?> resourceSelector, ICondition... conditions) {
		this.resourceSelector = resourceSelector;
		this.conditions = conditions;
	}

	public ConditionedResourceSelector(ResourceSelector<?> resourceSelector) {
		this(resourceSelector, NO_CONDITIONS);
	}

	/**
	 * Deserializes a new {@link ConditionedResourceSelector} instance from a {@link JsonElement} instance.
	 *
	 * @param key     The name of the key the {@link JsonElement} is paired with.
	 * @param element A {@link JsonElement} instance to deserialize from.
	 * @return A new {@link ConditionedResourceSelector} instance from a {@link JsonElement} instance.
	 * @throws JsonParseException If a deserialization error occurs.
	 */
	public static ConditionedResourceSelector deserialize(String key, JsonElement element) throws JsonParseException {
		if (element instanceof JsonPrimitive primitive && primitive.isString()) {
			return new ConditionedResourceSelector(new NamesResourceSelector(new ResourceLocation(primitive.getAsString())));
		} else if (element instanceof JsonObject jsonObject) {
			if (!GsonHelper.isValidNode(jsonObject, "conditions") || CraftingHelper.processConditions(GsonHelper.getAsJsonArray(jsonObject, "conditions"), ICondition.IContext.EMPTY)) {
				String type = GsonHelper.getAsString(jsonObject, "type");
				ResourceSelector.Serializer<?> serializer = ResourceSelectorSerializers.INSTANCE.getSerializer(type);
				if (serializer != null)
					return new ConditionedResourceSelector(serializer.deserialize(jsonObject.get("config")));
				throw new JsonParseException("Unknown selector type: " + type);
			}
			return EMPTY;
		} else if (element == null) throw new JsonParseException("Missing '" + key + "' member!");
		throw new JsonParseException("'" + key + "' must be a string or object!");
	}

	/**
	 * Serializes this as a {@link JsonElement} instance.
	 *
	 * @return A {@link JsonElement} representation of this {@link ConditionedResourceSelector} instance.
	 */
	public JsonElement serialize() {
		var conditions = this.conditions;
		boolean hasConditions = conditions != null && conditions.length > 0;
		ResourceSelector<?> selector = this.resourceSelector;
		if (!hasConditions && selector instanceof NamesResourceSelector namesResourceSelector) {
			var names = namesResourceSelector.names();
			if (names.size() == 1) return new JsonPrimitive(names.iterator().next().toString());
		}
		ResourceSelector.Serializer<?> serializer = selector.getSerializer();
		String type = ResourceSelectorSerializers.INSTANCE.getSerializerID(serializer);
		if (type == null) throw new JsonParseException("Could not find name for selector serializer: " + serializer);
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("type", new JsonPrimitive(type));
		jsonObject.add("config", selector.serialize());
		if (hasConditions) {
			JsonArray conditionsArray = new JsonArray();
			for (ICondition condition : conditions) {
				conditionsArray.add(CraftingHelper.serialize(condition));
			}
			jsonObject.add("conditions", conditionsArray);
		}
		return jsonObject;
	}

	public Either<Set<ResourceLocation>, Predicate<ResourceLocation>> select() {
		return this.resourceSelector.select();
	}

	/**
	 * Gets the {@link #resourceSelector}.
	 *
	 * @return The {@link #resourceSelector}.
	 */
	public ResourceSelector<?> getResourceSelector() {
		return this.resourceSelector;
	}

	/**
	 * Gets the array of {@link #conditions}.
	 *
	 * @return The array of {@link #conditions}.
	 */
	public ICondition[] getConditions() {
		return this.conditions;
	}
}
