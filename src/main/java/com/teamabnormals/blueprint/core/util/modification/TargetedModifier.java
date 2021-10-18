package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that holds a list of {@link ConfiguredModifier}s targeted on an object matched with a stored {@link ResourceLocation}.
 *
 * @param <T> The type of the object to modify.
 * @param <S> The type of the additional serialization object.
 * @param <D> The type of the additional deserialization object.
 * @author SmellyModder (Luke Tonon)
 * @see ConfiguredModifier
 * @see IModifier
 * @see ModifierRegistry
 */
public final class TargetedModifier<T, S, D> {
	private final ResourceLocation target;
	private final List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers;

	public TargetedModifier(ResourceLocation target, List<ConfiguredModifier<T, ?, S, D, ?>> configuredModifiers) {
		this.target = target;
		this.configuredModifiers = configuredModifiers;
	}

	/**
	 * Deserializes a {@link TargetedModifier} from a {@link JsonObject} with default parameters.
	 *
	 * @param object     A {@link JsonObject} to deserialize a {@link TargetedModifier} from.
	 * @param additional An additional deserialization object.
	 * @param registry   A {@link ModifierRegistry} to lookup {@link IModifier}s from.
	 * @param <T>        The type of the object to modify.
	 * @param <S>        The type of the additional serialization object.
	 * @param <D>        The type of the additional deserialization object.
	 * @return A deserialized {@link TargetedModifier} from a {@link JsonObject}.
	 * @throws JsonParseException If an error occurs when parsing the {@link JsonObject}.
	 * @see #deserialize(JsonObject, String, Object, ModifierRegistry, boolean)
	 */
	public static <T, S, D> TargetedModifier<T, S, D> deserialize(JsonObject object, D additional, ModifierRegistry<T, S, D> registry) {
		return deserialize(object, "target", additional, registry, true);
	}

	/**
	 * Deserializes a {@link TargetedModifier} from a {@link JsonObject}.
	 * <p>Use {@link #deserialize(JsonObject, Object, ModifierRegistry)} instead when possible.</p>
	 *
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
	public static <T, S, D> TargetedModifier<T, S, D> deserialize(JsonObject object, String targetKey, D additional, ModifierRegistry<T, S, D> registry, boolean logSkipping) throws JsonParseException {
		ResourceLocation target = new ResourceLocation(GsonHelper.getAsString(object, targetKey));
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
				Blueprint.LOGGER.info("Skipped modifier \"" + type + "\" for target \"" + target + "\" as its conditions were not met");
			}
		});
		return new TargetedModifier<>(target, configuredModifiers);
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
		jsonObject.addProperty(targetKey, this.target.toString());
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
	 * Gets the {@link ResourceLocation} for the target object to modify.
	 *
	 * @return The {@link ResourceLocation} for the target object to modify.
	 */
	public ResourceLocation getTarget() {
		return this.target;
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
