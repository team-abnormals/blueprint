package com.teamabnormals.blueprint.common.loot.modification.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.LootModifierSerializers;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LootModifier} implementation that modifies the pools in a {@link LootTable}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record LootPoolsModifier(List<LootPool> pools, boolean replace) implements LootModifier<LootPoolsModifier> {
	public static final Field POOLS = ObfuscationReflectionHelper.findField(LootTable.class, "f_79109_");

	@SuppressWarnings("unchecked")
	@Override
	public void modify(LootTableLoadEvent event) {
		try {
			if (this.replace) {
				POOLS.set(event.getTable(), this.pools);
			} else {
				((List<LootPool>) POOLS.get(event.getTable())).addAll(this.pools);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Serializer getSerializer() {
		return LootModifierSerializers.POOLS;
	}

	public static final class Serializer implements LootModifier.Serializer<LootPoolsModifier> {
		@Override
		public JsonElement serialize(LootPoolsModifier modifier, Gson gson) throws JsonParseException {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("replace", modifier.replace);
			JsonArray pools = new JsonArray();
			for (LootPool pool : modifier.pools) {
				pools.add(gson.toJsonTree(pool));
			}
			jsonObject.add("pools", pools);
			return jsonObject;
		}

		@Override
		public LootPoolsModifier deserialize(JsonElement element, Pair<Gson, LootDataManager> additional) throws JsonParseException {
			JsonObject jsonObject = element.getAsJsonObject();
			List<LootPool> lootPools = new ArrayList<>();
			JsonArray poolsArray = jsonObject.getAsJsonArray("pools");
			Gson gson = additional.getFirst();
			poolsArray.forEach(entry -> lootPools.add(gson.fromJson(entry, LootPool.class)));
			return new LootPoolsModifier(lootPools, jsonObject.get("replace").getAsBoolean());
		}
	}
}
