package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;

/**
 * The record class for representing a weighted slice of the world that uses a {@link BiomeUtil.ModdedBiomeProvider} instance for selecting biomes.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ModdedBiomeSlice(ResourceLocation name, int weight, BiomeUtil.ModdedBiomeProvider provider) {
	public static final Codec<ModdedBiomeSlice> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				ResourceLocation.CODEC.fieldOf("name").forGetter(slice -> slice.name),
				ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(slice -> slice.weight),
				BiomeUtil.ModdedBiomeProvider.CODEC.fieldOf("provider").forGetter(slice -> slice.provider)
		).apply(instance, ModdedBiomeSlice::new);
	});
	private static final Pair<ConditionedResourceSelector, ModdedBiomeSlice> SKIPPED = Pair.of(ConditionedResourceSelector.EMPTY, new ModdedBiomeSlice(new ResourceLocation(Blueprint.MOD_ID, "skipped"), 0, new BiomeUtil.OriginalModdedBiomeProvider()));

	/**
	 * Deserializes a {@link JsonElement} instance into a pair containing a {@link ConditionedResourceSelector} instance and a {@link ModdedBiomeSlice} instance.
	 *
	 * @param name    The name of the slice getting deserialized.
	 * @param element A {@link JsonElement} instance to deserialize.
	 * @param ops     A {@link DynamicOps} instance to use for decoding {@link BiomeUtil.ModdedBiomeProvider} instances.
	 * @return A pair containing a new {@link ConditionedResourceSelector} instance and a new {@link ModdedBiomeSlice} instance from a {@link JsonElement} instance.
	 * @throws JsonParseException If a problem occurs when deserializing.
	 */
	public static Pair<ConditionedResourceSelector, ModdedBiomeSlice> deserializeWithSelector(ResourceLocation name, JsonElement element, DynamicOps<JsonElement> ops) throws JsonParseException {
		JsonObject object = GsonHelper.convertToJsonObject(element, element.toString());
		ConditionedResourceSelector selector = ConditionedResourceSelector.deserialize("selector", object.get("selector"));
		if (selector == ConditionedResourceSelector.EMPTY) {
			Blueprint.LOGGER.info("Skipped modded biome slice named '" + name + "' as its conditions were not met");
			return SKIPPED;
		}
		var sliceResult = CODEC.decode(ops, object);
		var sliceError = sliceResult.error();
		if (sliceError.isPresent()) throw new JsonParseException(sliceError.get().message());
		return Pair.of(selector, sliceResult.result().get().getFirst());
	}

	/**
	 * Serializes a {@link ConditionedResourceSelector} instance and this {@link ModdedBiomeSlice} instance into a {@link JsonObject} instance.
	 *
	 * @param selector A {@link ConditionedResourceSelector} instance to serialize with this slice.
	 * @param ops      A {@link DynamicOps} instance to use for encoding the {@link #provider}.
	 * @return A {@link JsonObject} instance representing a {@link ModdedBiomeSlice} instance with a selector.
	 * @throws JsonParseException If a problem occurs when serializing.
	 */
	public JsonElement serializeWithSelector(ConditionedResourceSelector selector, DynamicOps<JsonElement> ops) throws JsonParseException {
		JsonObject object = new JsonObject();
		object.add("selector", selector.serialize());
		var result = CODEC.encode(this, ops, object);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return result.get().left().get();
	}
}
