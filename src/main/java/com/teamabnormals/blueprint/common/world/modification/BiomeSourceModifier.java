package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.modification.targeting.ConditionedModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.ConfiguredModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.ModifierTargetSelectorRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Collections;

/**
 * The simple record class for representing a configured modifier that adds a {@link BiomeUtil.ModdedBiomeProvider} instance for a specific list of level targets.
 * <p>Even though these are 'chunk generator modifiers', we keep them as a separate system for performance and structural reasons.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public record BiomeSourceModifier(ConditionedModifierTargetSelector<?, ?> targetSelector, BiomeUtil.ModdedBiomeProvider provider) {
	private static final BiomeUtil.ModdedBiomeProvider SKIPPED_PROVIDER = new BiomeUtil.OriginalModdedBiomeProvider(new ResourceLocation(Blueprint.MOD_ID, "skipped"), 0);

	/**
	 * Deserializes a {@link JsonElement} instance into a {@link BiomeSourceModifier} instance.
	 *
	 * @param name    The name of the modifier getting deserialized.
	 * @param element A {@link JsonElement} instance to deserialize.
	 * @param ops     A {@link DynamicOps} instance to use for decoding {@link BiomeUtil.ModdedBiomeProvider} instances.
	 * @return Deserializes new {@link BiomeSourceModifier} instance from a {@link JsonElement} instance.
	 * @throws JsonParseException If a problem occurs when deserializing.
	 */
	public static BiomeSourceModifier deserialize(ResourceLocation name, JsonElement element, DynamicOps<JsonElement> ops) throws JsonParseException {
		JsonObject object = GsonHelper.convertToJsonObject(element, element.toString());
		JsonElement targetElement = object.get("target");
		ConditionedModifierTargetSelector<?, ?> selector;
		if (targetElement instanceof JsonPrimitive) {
			selector = new ConditionedModifierTargetSelector<>(ModifierTargetSelectorRegistry.NAMES.withConfiguration(Collections.singletonList(new ResourceLocation(targetElement.getAsString()))));
		} else if (targetElement instanceof JsonObject targetObject) {
			selector = new ConditionedModifierTargetSelector<>(ConfiguredModifierTargetSelector.deserialize(targetObject));
			if (selector.getTargetSelector() == ConfiguredModifierTargetSelector.EMPTY) {
				Blueprint.LOGGER.info("Skipped biome source modifier named " + name + " as its conditions were not met");
				return new BiomeSourceModifier(selector, SKIPPED_PROVIDER);
			}
		} else throw new JsonParseException("'target' must be a string or object!");
		var providerResult = BiomeUtil.ModdedBiomeProvider.CODEC.decode(ops, GsonHelper.getAsJsonObject(object, "provider"));
		var providerError = providerResult.error();
		if (providerError.isPresent()) throw new JsonParseException(providerError.get().message());
		return new BiomeSourceModifier(selector, providerResult.result().get().getFirst());
	}

	/**
	 * Serializes this {@link BiomeSourceModifier} instance into a {@link JsonObject} instance.
	 *
	 * @param ops A {@link DynamicOps} instance to use for encoding the {@link #provider}.
	 * @return A {@link JsonObject} instance representing this {@link BiomeSourceModifier} instance.
	 * @throws JsonParseException If a problem occurs when serializing.
	 */
	public JsonObject serialize(DynamicOps<JsonElement> ops) throws JsonParseException {
		JsonObject object = new JsonObject();
		object.add("target", this.targetSelector.serialize());
		var result = BiomeUtil.ModdedBiomeProvider.CODEC.encodeStart(ops, this.provider);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		object.add("provider", result.get().left().get());
		return object;
	}
}
