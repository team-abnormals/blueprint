package com.minecraftabnormals.abnormals_core.core.endimator;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.endimator.instructions.EndimationInstructionList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * Handles all the Data Driven Endimation internals.
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationDataManager extends JsonReloadListener {
	private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(EndimationInstructionList.class, new EndimationInstructionListDeserializer()).create();
	public static final Map<ResourceLocation, EndimationInstructionList> ENDIMATIONS = Maps.newHashMap();
	
	public EndimationDataManager() {
		super(GSON, "endimations");
	}
	
	public static class EndimationInstructionListDeserializer implements JsonDeserializer<EndimationInstructionList> {
		@Override
		public EndimationInstructionList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			DataResult<Pair<EndimationInstructionList, JsonElement>> decode = EndimationInstructionList.CODEC.decode(JsonOps.INSTANCE, json.getAsJsonObject());
			
			Optional<Pair<EndimationInstructionList, JsonElement>> result = decode.result();
			if (result.isPresent()) {
				return result.get().getFirst();
			}

			decode.error().ifPresent(error -> AbnormalsCore.LOGGER.error("Failed to decode Endimation: {}", error.toString()));
			return null;
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> resourceMap, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		for (Map.Entry<ResourceLocation, JsonElement> entry : resourceMap.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;
			
			try {
				EndimationInstructionList instructions = GSON.fromJson(entry.getValue(), EndimationInstructionList.class);
				ENDIMATIONS.put(resourcelocation, instructions);
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				AbnormalsCore.LOGGER.error("Parsing error loading Endimation {}", resourcelocation, jsonparseexception);
			}
		}
		AbnormalsCore.LOGGER.info("Endimation Data Manager has loaded {} Endimations", ENDIMATIONS.size());
	}
}