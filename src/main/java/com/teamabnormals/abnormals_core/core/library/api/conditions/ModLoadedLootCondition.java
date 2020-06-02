package com.teamabnormals.abnormals_core.core.library.api.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.fml.ModList;

public class ModLoadedLootCondition implements ILootCondition {
   private final String modid;
   
   public ModLoadedLootCondition(String modid) {
      this.modid = modid;
   }

   public boolean test(LootContext context) {
      return ModList.get().isLoaded(this.modid);
   }

   public static ILootCondition.IBuilder builder(String modid) {
	   return () -> {
		   return new ModLoadedLootCondition(modid);
	   };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<ModLoadedLootCondition> {
	   public Serializer() {
		   super(new ResourceLocation(AbnormalsCore.MODID, "mod_loaded"), ModLoadedLootCondition.class);
	   }
	   
	   public void serialize(JsonObject json, ModLoadedLootCondition value, JsonSerializationContext context) {
		   json.addProperty("modid", value.modid);
	   }
	   
	   public ModLoadedLootCondition deserialize(JsonObject json, JsonDeserializationContext context) {
		   return new ModLoadedLootCondition(JSONUtils.getString(json, "modid"));
	   }
   }
}