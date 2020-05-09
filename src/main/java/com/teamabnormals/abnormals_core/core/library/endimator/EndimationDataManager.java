package com.teamabnormals.abnormals_core.core.library.endimator;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

/**
 * Handles all the Data Driven Endimation internals
 * @author SmellyModder
 */
public class EndimationDataManager extends JsonReloadListener {
	private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(EndimationConversion.class, new EndimationConversion.Serializer()).create();
	public static final Map<ResourceLocation, EndimationConversion> ENDIMATIONS = Maps.newHashMap();
	
	public EndimationDataManager() {
		super(GSON, "endimations");
	}

	public static class EndimationConversion {
		private List<EndimationInstruction> instructions = Lists.newArrayList();
		
		public EndimationConversion(List<EndimationInstruction> instructions) {
			this.instructions = instructions;
		}
		
		public List<EndimationInstruction> getInstructions() {
			return this.instructions;
		}
		
		public static class Serializer implements JsonDeserializer<EndimationConversion>, JsonSerializer<EndimationConversion> {

			@Override
			public JsonElement serialize(EndimationConversion conversion, Type typeOfSrc, JsonSerializationContext context) {
				JsonObject json = new JsonObject();
				if(!conversion.instructions.isEmpty()) {
					JsonArray instructions = new JsonArray();
					for(EndimationInstruction instruction : conversion.instructions) {
						instructions.add(instruction.serialize());
					}
					json.add("instructions", instructions);
				}
				return json;
			}

			@Override
			public EndimationConversion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				JsonObject object = json.getAsJsonObject();
				List<EndimationInstruction> list = Lists.newArrayList();
				if(object.has("instructions")) {
					JsonArray instructions = JSONUtils.getJsonArray(object, "instructions");
					for(int i = 0; i < instructions.size(); i++) {
						JsonObject entry = instructions.get(i).getAsJsonObject();
						list.add(EndimationInstruction.deserialize(entry));
					}
				}
				return new EndimationConversion(list);
			}
			
		}
	}
	
	public static class EndimationInstruction {
		public final InstructionType type;
		public final int tickLength;
		
		public EndimationInstruction(InstructionType type, int tickLength) {
			this.type = type;
			this.tickLength = tickLength;
		}
		
		public JsonObject serialize() {
			JsonObject json = new JsonObject();
			json.addProperty("type", this.type.toString().toLowerCase());
			
			if(this.type != InstructionType.END_KEYFRAME) {
				json.addProperty("ticks", this.tickLength);
			}
			return json;
		}
		
		public static EndimationInstruction deserialize(JsonObject json) {
			String stringType = json.get("type").getAsString();
			int tickLength = json.has("ticks") ? json.get("ticks").getAsInt() : 0;
			InstructionType type = InstructionType.getTypeByString(stringType);
			if(type != InstructionType.START_KEYFRAME && type != InstructionType.END_KEYFRAME && type != InstructionType.RESET_KEYFRAME && type != InstructionType.STATIC_KEYFRAME) {
				return new ModelRendererEndimationInstruction(type, json.get("model_renderer").getAsString(), json.get("x").getAsFloat(), json.get("y").getAsFloat(), json.get("z").getAsFloat());
			}
			return new EndimationInstruction(type, tickLength);
		}
		
		public static class ModelRendererEndimationInstruction extends EndimationInstruction {
			public final String modelRenderer;
			public final float x;
			public final float y;
			public final float z;
			
			public ModelRendererEndimationInstruction(InstructionType type, String modelRenderer, float x, float y, float z) {
				super(type, 0);
				this.modelRenderer = modelRenderer;
				this.x = x;
				this.y = y;
				this.z = z;
			}
			
			@Override
			public JsonObject serialize() {
				JsonObject json = super.serialize();
				json.addProperty("model_renderer", this.modelRenderer);
				json.addProperty("x", this.x);
				json.addProperty("y", this.y);
				json.addProperty("z", this.z);
				return json;
			}
		}
	}
	
	public enum InstructionType {
		START_KEYFRAME(),
		END_KEYFRAME(),
		STATIC_KEYFRAME(),
		RESET_KEYFRAME(),
		MOVE(),
		ROTATE(),
		OFFSET(),
		ADD_MOVE(),
		ADD_ROTATE(),
		ADD_OFFSET();
		
		public static InstructionType getTypeByString(String name) {
			for(InstructionType types : values()) {
				if(types.toString().toLowerCase().equals(name)) {
					return types;
				}
			}
			return InstructionType.START_KEYFRAME;
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> resourceMap, IResourceManager resourceManager, IProfiler profiler) {
		for(Map.Entry<ResourceLocation, JsonObject> entry : resourceMap.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if(resourcelocation.getPath().startsWith("_")) continue;
			
			try {
				EndimationConversion conversion = GSON.fromJson(entry.getValue(), EndimationConversion.class);
				ENDIMATIONS.put(resourcelocation, conversion);
			} catch(IllegalArgumentException | JsonParseException jsonparseexception) {
				AbnormalsCore.LOGGER.error("Parsing error loading Endimation {}", resourcelocation, jsonparseexception);
			}
		}
		AbnormalsCore.LOGGER.info("Endimation Data Manager has Loaded {} Endimations", ENDIMATIONS.size());
	}
}