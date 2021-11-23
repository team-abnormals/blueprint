package com.teamabnormals.blueprint.core.util.modification.targeting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Unit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class that represents a configured {@link ModifierTargetSelector}.
 *
 * @param <C> The type of config for the type of {@link ModifierTargetSelector}.
 * @param <S> The type of {@link ModifierTargetSelector} to configure for.
 * @author SmellyModder (Luke Tonon)
 */
public final class ConfiguredModifierTargetSelector<C, S extends ModifierTargetSelector<C>> {
	public static final ConfiguredModifierTargetSelector<Unit, ModifierTargetSelector<Unit>> EMPTY = ModifierTargetSelectorRegistry.EMPTY.withConfiguration(Unit.INSTANCE);
	private final S selector;
	private final C config;

	public ConfiguredModifierTargetSelector(S selector, C config) {
		this.selector = selector;
		this.config = config;
	}

	/**
	 * Deserializes a new {@link ConfiguredModifierTargetSelector} from a given {@link JsonObject} instance.
	 *
	 * @param jsonObject A {@link JsonObject} instance to deserialize from.
	 * @return A new {@link ConfiguredModifierTargetSelector} from a given {@link JsonObject} instance.
	 * @throws JsonParseException If a deserialization error occurs.
	 */
	public static ConfiguredModifierTargetSelector<?, ?> deserialize(JsonObject jsonObject) throws JsonParseException {
		if (!GsonHelper.isValidNode(jsonObject, "conditions") || CraftingHelper.processConditions(GsonHelper.getAsJsonArray(jsonObject, "conditions"))) {
			String type = GsonHelper.getAsString(jsonObject, "type");
			ModifierTargetSelector<?> targetSelector = ModifierTargetSelectorRegistry.INSTANCE.getTargetSelector(type);
			if (targetSelector != null) {
				return targetSelector.deserializeConfigured(jsonObject.get("config"));
			}
			throw new JsonParseException("Unknown selector type: " + type);
		}
		return EMPTY;
	}

	/**
	 * Serializes this {@link ConfiguredModifierTargetSelector} as a {@link JsonObject} instance.
	 *
	 * @return A {@link JsonObject} representation of this {@link ConfiguredModifierTargetSelector}.
	 */
	public JsonObject serialize() {
		JsonObject jsonObject = new JsonObject();
		S selector = this.selector;
		jsonObject.addProperty("type", ModifierTargetSelectorRegistry.INSTANCE.getSelectorID(selector));
		jsonObject.add("config", selector.serialize(this.config));
		return jsonObject;
	}

	/**
	 * Gets a list of {@link ResourceLocation} names to target from a set of resources.
	 *
	 * @param resources A set of resources to use for getting the target names.
	 * @return A list of {@link ResourceLocation} names to target from a set of resources.
	 */
	public List<ResourceLocation> getTargetNames(Set<Map.Entry<ResourceLocation, JsonElement>> resources) {
		return this.selector.getTargetNames(resources, this.config);
	}

	/**
	 * Gets the {@link #selector}.
	 *
	 * @return The {@link #selector}.
	 */
	public S getSelector() {
		return this.selector;
	}

	/**
	 * Gets the {@link #config}.
	 *
	 * @return The {@link #config}.
	 */
	public C getConfig() {
		return this.config;
	}
}
