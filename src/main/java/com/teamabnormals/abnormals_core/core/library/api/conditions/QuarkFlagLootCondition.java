package com.teamabnormals.abnormals_core.core.library.api.conditions;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.fml.ModList;

/**
 * Loot Condition that uses Quark's flags without making use of Quark's classes
 * @author SmellyModder(Luke Tonon)
 */
public class QuarkFlagLootCondition implements ILootCondition {	
	private final String flag;
	
	public QuarkFlagLootCondition(String flag) {
		this.flag = flag;
	}

	public boolean test(LootContext context) {
		if(ModList.get().isLoaded("quark")) {
			JsonObject dummyObject = new JsonObject();
			dummyObject.addProperty("flag", this.flag);
			return ((ILootCondition) LootConditionManager.getSerializerForName(new ResourceLocation("quark:flag")).deserialize(dummyObject, new DummyJsonContext())).test(context);
		}
		return false;
	}

	public static ILootCondition.IBuilder builder(String flag) {
		return () -> {
			return new QuarkFlagLootCondition(flag);
		};
	}

	public static class Serializer extends ILootCondition.AbstractSerializer<QuarkFlagLootCondition> {
		
		public Serializer() {
			super(new ResourceLocation(AbnormalsCore.MODID, "quark_flag"), QuarkFlagLootCondition.class);
		}
		   
		public void serialize(JsonObject json, QuarkFlagLootCondition value, JsonSerializationContext context) {
			json.addProperty("flag", value.flag);
		}
		
		public QuarkFlagLootCondition deserialize(JsonObject json, JsonDeserializationContext context) {
			return new QuarkFlagLootCondition(JSONUtils.getString(json, "flag"));
		}
		
	}
	
	private final class DummyJsonContext implements JsonSerializationContext, JsonDeserializationContext {
		
		@Override 
		public JsonElement serialize(Object src) {
			return null;
		}
	    
		@Override 
		public JsonElement serialize(Object src, Type typeOfSrc) {
			return null;
		}
	    
		@Override 
		public <R> R deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
			return null;
		}
		
	};
}