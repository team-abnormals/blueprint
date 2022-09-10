package com.teamabnormals.blueprint.core.util.modification;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of prioritized {@link ObjectModifier} instances and a {@link ConditionedResourceSelector} instance to select the resources that the modifiers will modify.
 *
 * @param <T> The type of the object to modify.
 * @param <S> The type of the additional serialization object.
 * @param <D> The type of the additional deserialization object.
 * @author SmellyModder (Luke Tonon)
 * @see ConditionedResourceSelector
 * @see ObjectModifier
 * @see ObjectModifierSerializerRegistry
 */
public record ObjectModifierGroup<T, S, D>(ConditionedResourceSelector selector, List<ObjectModifier<T, S, D, ?>> modifiers, EventPriority priority) {
	/**
	 * Deserializes an {@link EventPriority} instance from a {@link JsonObject} instance.
	 *
	 * @param object A {@link JsonObject} instance to get the "priority" member from.
	 * @return An {@link EventPriority} instance from a {@link JsonObject} instance.
	 */
	public static EventPriority deserializePriority(JsonObject object) {
		if (object.has("priority")) {
			String priorityName = GsonHelper.getAsString(object, "priority").toUpperCase();
			for (EventPriority priority : EventPriority.values()) {
				if (priority.name().equals(priorityName)) {
					return priority;
				}
			}
			throw new JsonParseException("Unknown priority type: " + priorityName);
		}
		return EventPriority.NORMAL;
	}

	/**
	 * Deserializes a {@link ObjectModifierGroup} instance from a {@link JsonObject} instance.
	 *
	 * @param name          The name of the {@link JsonObject} instance to deserialize.
	 * @param object        A {@link JsonObject} to deserialize a {@link ObjectModifierGroup} from.
	 * @param additional    An additional deserialization object.
	 * @param registry      A {@link ObjectModifierSerializerRegistry} for identifying {@link ObjectModifier.Serializer} instances.
	 * @param logSkipping   If groups that didn't meet their conditions should get logged.
	 * @param allowPriority If {@link #priority} should get considered when deserializing.
	 * @param <T>           The type of the object to modify.
	 * @param <S>           The type of the additional serialization object.
	 * @param <D>           The type of the additional deserialization object.
	 * @return A deserialized {@link ObjectModifierGroup} instance from a {@link JsonObject} instance.
	 * @throws JsonParseException If an error occurs when parsing the {@link JsonObject} instance.
	 */
	@SuppressWarnings("unchecked")
	public static <T, S, D> ObjectModifierGroup<T, S, D> deserialize(String name, JsonObject object, D additional, ObjectModifierSerializerRegistry<T, S, D> registry, boolean logSkipping, boolean allowPriority) throws JsonParseException {
		EventPriority priority = allowPriority ? deserializePriority(object) : EventPriority.NORMAL;
		ConditionedResourceSelector selector = ConditionedResourceSelector.deserialize("selector", object.get("selector"));
		if (selector == ConditionedResourceSelector.EMPTY) {
			if (logSkipping) Blueprint.LOGGER.info("Skipped modifier group named '" + name + "' as its conditions were not met");
			return new ObjectModifierGroup<>(selector, ImmutableList.of(), priority);
		}
		List<ObjectModifier<T, S, D, ?>> objectModifiers = new ArrayList<>();
		GsonHelper.getAsJsonArray(object, "modifiers").forEach(element -> {
			JsonObject entry = element.getAsJsonObject();
			if (!GsonHelper.isValidNode(entry, "conditions") || CraftingHelper.processConditions(GsonHelper.getAsJsonArray(entry, "conditions"))) {
				String type = GsonHelper.getAsString(entry, "type");
				ObjectModifier.Serializer<? extends ObjectModifier<T, S, D, ?>, S, D> serializer = registry.getSerializer(type);
				if (serializer == null) throw new JsonParseException("Unknown modifier type: " + type);
				JsonElement config = entry.get("config");
				if (config == null) throw new JsonParseException("Missing 'config' element!");
				objectModifiers.add(serializer.deserialize(config, additional));
			} else if (logSkipping) Blueprint.LOGGER.info("Skipped modifier named '" + name + "' as its conditions were not met");
		});
		return new ObjectModifierGroup<>(selector, objectModifiers, priority);
	}

	/**
	 * Serializes this {@link ObjectModifierGroup} into a {@link JsonObject} instance.
	 *
	 * @param additional An additional serialization object of type {@code <S>}.
	 * @param registry   A {@link ObjectModifierSerializerRegistry} instance for identifying {@link ObjectModifier.Serializer} instances.
	 * @param conditions A two-dimensional array of {@link ICondition} instances. First indexes correspond to the order of {@link #modifiers}.
	 * @return A {@link JsonObject} instance representing this {@link ObjectModifierGroup} instance with conditions.
	 * @throws JsonParseException If an error occurs when serializing.
	 */
	public JsonObject serialize(S additional, ObjectModifierSerializerRegistry<T, S, D> registry, ICondition[][] conditions) throws JsonParseException {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("selector", this.selector.serialize());
		jsonObject.addProperty("priority", this.priority.toString().toLowerCase());
		var objectModifiers = this.modifiers;
		JsonArray modifiers = new JsonArray();
		for (int i = 0; i < objectModifiers.size(); i++) {
			var modifier = objectModifiers.get(i);
			var serializer = modifier.getSerializer();
			String name = registry.getName(serializer);
			if (name == null) throw new JsonParseException("Could not find name for modifier serializer: " + serializer);
			JsonObject modifierObject = new JsonObject();
			modifierObject.addProperty("type", name);
			modifierObject.add("config", modifier.serialize(additional));
			var modifierConditions = conditions[i];
			if (modifierConditions.length > 0) {
				JsonArray conditionsObject = new JsonArray();
				for (ICondition condition : modifierConditions) {
					conditionsObject.add(CraftingHelper.serialize(condition));
				}
				modifierObject.add("conditions", conditionsObject);
			}
			modifiers.add(modifierObject);
		}
		jsonObject.add("modifiers", modifiers);
		return jsonObject;
	}
}
