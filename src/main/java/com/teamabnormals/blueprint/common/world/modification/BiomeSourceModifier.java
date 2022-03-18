package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

/**
 * The simple record class for representing a configured modifier that adds a {@link BiomeUtil.ModdedBiomeProvider} instance for a specific list of level targets.
 * <p>Even though these are 'chunk generator modifiers', we keep them as a separate system for performance and structural reasons.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public record BiomeSourceModifier(ConditionedResourceSelector selector, BiomeUtil.ModdedBiomeProvider provider) {
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
		ConditionedResourceSelector selector = ConditionedResourceSelector.deserialize("target", object.get("target"));
		if (selector == ConditionedResourceSelector.EMPTY) {
			Blueprint.LOGGER.info("Skipped biome source modifier named '" + name + "' as its conditions were not met");
			return new BiomeSourceModifier(selector, SKIPPED_PROVIDER);
		}
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
		object.add("target", this.selector.serialize());
		var result = BiomeUtil.ModdedBiomeProvider.CODEC.encodeStart(ops, this.provider);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		object.add("provider", result.get().left().get());
		return object;
	}
}
