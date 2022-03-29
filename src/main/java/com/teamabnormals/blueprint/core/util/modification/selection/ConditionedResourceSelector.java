package com.teamabnormals.blueprint.core.util.modification.selection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.EmptyResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.List;

/**
 * The class that represents a {@link ResourceSelector} with conditions.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ConditionedResourceSelector {
	public static final ConditionedResourceSelector EMPTY = new ConditionedResourceSelector(new EmptyResourceSelector());
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
			if (!GsonHelper.isValidNode(jsonObject, "conditions") || CraftingHelper.processConditions(GsonHelper.getAsJsonArray(jsonObject, "conditions"))) {
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
		boolean hasConditions = conditions.length > 0;
		ResourceSelector<?> selector = this.resourceSelector;
		if (!hasConditions && selector instanceof NamesResourceSelector namesResourceSelector) {
			var names = namesResourceSelector.names();
			if (names.size() == 1) return new JsonPrimitive(names.get(0).toString());
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

	/**
	 * Selects a list of {@link ResourceLocation} names from a {@link SelectionSpace} instance.
	 *
	 * @param space A {@link SelectionSpace} instance to use for selecting the names.
	 * @return A list of {@link ResourceLocation} names from a {@link SelectionSpace} instance.
	 */
	public List<ResourceLocation> select(SelectionSpace space) {
		return this.resourceSelector.select(space);
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
