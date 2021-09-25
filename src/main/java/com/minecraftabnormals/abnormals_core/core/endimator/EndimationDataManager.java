package com.minecraftabnormals.abnormals_core.core.endimator;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.endimator.instructions.EndimationInstructionList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * Handles all the Data Driven Endimation internals.
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationDataManager extends SimpleJsonResourceReloadListener {
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
	protected void apply(Map<ResourceLocation, JsonElement> resourceMap, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
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