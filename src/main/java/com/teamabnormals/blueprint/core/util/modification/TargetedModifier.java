package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that holds a list of prioritized {@link ConfiguredModifier} instances targeted according to a {@link ConditionedResourceSelector} instance.
 *
 * @param <T> The type of the object to modify.
 * @param <S> The type of the additional serialization object.
 * @param <D> The type of the additional deserialization object.
 * @author SmellyModder (Luke Tonon)
 * @see ConditionedResourceSelector
 * @see ConfiguredModifier
 * @see IModifier
 * @see ModifierRegistry
 */
public final class TargetedModifier<T, S, D> {
	private final ConditionedResourceSelector resourceSelector;
	private final EventPriority priority;
	private final List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers;

	public TargetedModifier(ConditionedResourceSelector resourceSelector, EventPriority priority, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		this.resourceSelector = resourceSelector;
		this.priority = priority;
		this.configuredModifiers = configuredModifiers;
	}

	public TargetedModifier(ConditionedResourceSelector resourceSelector, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		this(resourceSelector, EventPriority.NORMAL, configuredModifiers);
	}

	public TargetedModifier(ResourceSelector<?> resourceSelector, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		this(new ConditionedResourceSelector(resourceSelector), EventPriority.NORMAL, configuredModifiers);
	}

	/**
	 * Deserializes a {@link TargetedModifier} from a {@link JsonObject} with default parameters.
	 *
	 * @param name       The name of the {@link JsonObject} instance to deserialize.
	 * @param object     A {@link JsonObject} to deserialize a {@link TargetedModifier} from.
	 * @param additional An additional deserialization object.
	 * @param registry   A {@link ModifierRegistry} to lookup {@link IModifier}s from.
	 * @param <T>        The type of the object to modify.
	 * @param <S>        The type of the additional serialization object.
	 * @param <D>        The type of the additional deserialization object.
	 * @return A deserialized {@link TargetedModifier} from a {@link JsonObject}.
	 * @throws JsonParseException If an error occurs when parsing the {@link JsonObject}.
	 * @see #deserialize(String, JsonObject, String, Object, ModifierRegistry, boolean, boolean)
	 */
	public static <T, S, D> TargetedModifier<T, S, D> deserialize(String name, JsonObject object, D additional, ModifierRegistry<T, S, D> registry) {
		return deserialize(name, object, "target", additional, registry, true, true);
	}

	/**
	 * Deserializes a {@link TargetedModifier} from a {@link JsonObject}.
	 * <p>Use {@link #deserialize(String, JsonObject, Object, ModifierRegistry)} instead when possible.</p>
	 *
	 * @param name        The name of the {@link JsonObject} instance to deserialize.
	 * @param object      A {@link JsonObject} to deserialize a {@link TargetedModifier} from.
	 * @param targetKey   The key for the 'target' string.
	 * @param additional  An additional deserialization object.
	 * @param registry    A {@link ModifierRegistry} to lookup {@link IModifier}s from.
	 * @param logSkipping If this method should log the skipping of deserializing when there are false conditions.
	 * @param <T>         The type of the object to modify.
	 * @param <S>         The type of the additional serialization object.
	 * @param <D>         The type of the additional deserialization object.
	 * @return A deserialized {@link TargetedModifier} from a {@link JsonObject}.
	 * @throws JsonParseException If an error occurs when parsing the {@link JsonObject}.
	 */
	public static <T, S, D> TargetedModifier<T, S, D> deserialize(String name, JsonObject object, String targetKey, D additional, ModifierRegistry<T, S, D> registry, boolean logSkipping, boolean allowPriority) throws JsonParseException {
		EventPriority priority = null;
		if (allowPriority && object.has("priority")) {
			String priorityName = GsonHelper.getAsString(object, "priority").toUpperCase();
			for (EventPriority test : EventPriority.values()) {
				if (test.name().equals(priorityName)) {
					priority = test;
					break;
				}
			}
			if (priority == null) {
				throw new JsonParseException("Unknown priority type: " + priorityName);
			}
		} else priority = EventPriority.NORMAL;
		ConditionedResourceSelector selector = ConditionedResourceSelector.deserialize(targetKey, object.get(targetKey));
		if (selector == ConditionedResourceSelector.EMPTY) {
			if (logSkipping)
				Blueprint.LOGGER.info("Skipped modifier named '" + name + "' as its conditions were not met");
			return new TargetedModifier<>(selector, priority, new ArrayList<>());
		}
		List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers = new ArrayList<>();
		JsonArray modifiers = GsonHelper.getAsJsonArray(object, "modifiers");
		modifiers.forEach(element -> {
			JsonObject entry = element.getAsJsonObject();
			String type = GsonHelper.getAsString(entry, "type");
			if (!GsonHelper.isValidNode(entry, "conditions") || CraftingHelper.processConditions(GsonHelper.getAsJsonArray(entry, "conditions"))) {
				IModifier<T, ?, S, D> configuredModifier = registry.getModifier(type);
				if (configuredModifier == null) {
					throw new JsonParseException("Unknown modifier type: " + type);
				}
				JsonElement config = entry.get("config");
				if (config == null) {
					throw new JsonParseException("Missing 'config' element!");
				}
				configuredModifiers.add(configuredModifier.deserializeConfigured(config, additional));
			} else if (logSkipping) {
				Blueprint.LOGGER.info("Skipped modifier named '" + name + "' as its conditions were not met");
			}
		});
		return new TargetedModifier<>(selector, priority, configuredModifiers);
	}

	/**
	 * Serializes this {@link TargetedModifier} into a {@link JsonObject}.
	 *
	 * @param additional       An additional serialization object of type {@code <S>}.
	 * @param targetKey        A key to have to target name be assigned to in the {@link JsonObject}. Best to have this just be "target".
	 * @param modifierRegistry A {@link ModifierRegistry} to lookup the names of the {@link IModifier}s.
	 * @param conditions       A two dimensional array of {@link ICondition}s. First indexes correspond to the order of {@link #configuredModifiers}.
	 * @return A new serialized {@link JsonObject} of this {@link TargetedModifier}.
	 * @throws JsonParseException If an error occurs when serializing this {@link TargetedModifier}.
	 */
	public JsonObject serialize(S additional, String targetKey, ModifierRegistry<T, S, D> modifierRegistry, ICondition[][] conditions) throws JsonParseException {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(targetKey, this.resourceSelector.serialize());
		jsonObject.addProperty("priority", this.priority.toString().toLowerCase());
		JsonArray modifiers = new JsonArray();
		List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers = this.configuredModifiers;
		int conditionsLength = conditions.length;
		for (int i = 0; i < configuredModifiers.size(); i++) {
			ConfiguredModifier<T, ?, S, D, ?> configuredModifier = configuredModifiers.get(i);
			JsonObject object = new JsonObject();
			object.addProperty("type", modifierRegistry.getName(configuredModifier.getModifer()));
			if (i < conditionsLength) {
				JsonArray conditionsObject = new JsonArray();
				ICondition[] conditionsArray = conditions[i];
				for (ICondition condition : conditionsArray) {
					conditionsObject.add(CraftingHelper.serialize(condition));
				}
			}
			object.add("config", configuredModifier.serialize(additional));
			modifiers.add(object);
		}
		jsonObject.add("modifiers", modifiers);
		return jsonObject;
	}

	/**
	 * Gets the {@link ConditionedResourceSelector} to use for selecting objects to modify.
	 *
	 * @return The {@link ConditionedResourceSelector} to use for selecting objects to modify.
	 */
	public ConditionedResourceSelector getResourceSelector() {
		return this.resourceSelector;
	}

	/**
	 * Gets the {@link EventPriority} to use for prioritizing this modifier.
	 *
	 * @return The {@link EventPriority} to use for prioritizing this modifier.
	 */
	public EventPriority getPriority() {
		return this.priority;
	}

	/**
	 * Gets the {@link ConfiguredModifier}s that will be used on the target object.
	 *
	 * @return The {@link ConfiguredModifier}s that will be used on the target object.
	 */
	public List<ConfiguredModifier<T, ?, S, D, ?>> getConfiguredModifiers() {
		return this.configuredModifiers;
	}
}
