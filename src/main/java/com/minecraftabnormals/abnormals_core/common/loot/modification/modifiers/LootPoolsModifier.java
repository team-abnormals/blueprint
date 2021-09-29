package com.minecraftabnormals.abnormals_core.common.loot.modification.modifiers;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link ILootModifier} that modifies the pools in a {@link LootTable}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootPoolsModifier implements ILootModifier<LootPoolsModifier.Config> {
	public static final Field POOLS = ObfuscationReflectionHelper.findField(LootTable.class, "f_79109_");

	@SuppressWarnings("unchecked")
	@Override
	public void modify(LootTableLoadEvent event, Config config) {
		try {
			if (config.replace) {
				POOLS.set(event.getTable(), config.pools);
			} else {
				((List<LootPool>) POOLS.get(event.getTable())).addAll(config.pools);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement serialize(LootPoolsModifier.Config config, Gson gson) throws JsonParseException {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("replace", config.replace);
		JsonArray pools = new JsonArray();
		for (LootPool pool : config.pools) {
			pools.add(gson.toJsonTree(pool));
		}
		jsonObject.add("pools", pools);
		return jsonObject;
	}

	@Override
	public LootPoolsModifier.Config deserialize(JsonElement element, Pair<Gson, PredicateManager> additional) throws JsonParseException {
		JsonObject jsonObject = element.getAsJsonObject();
		List<LootPool> lootPools = new ArrayList<>();
		JsonArray poolsArray = jsonObject.getAsJsonArray("pools");
		Gson gson = additional.getFirst();
		poolsArray.forEach(entry -> lootPools.add(gson.fromJson(entry, LootPool.class)));
		return new LootPoolsModifier.Config(lootPools, jsonObject.get("replace").getAsBoolean());
	}

	public static class Config {
		private final List<LootPool> pools;
		private final boolean replace;

		public Config(List<LootPool> pools, boolean replace) {
			this.pools = pools;
			this.replace = replace;
		}
	}
}
