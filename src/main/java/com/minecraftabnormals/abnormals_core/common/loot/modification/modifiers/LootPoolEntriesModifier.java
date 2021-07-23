package com.minecraftabnormals.abnormals_core.common.loot.modification.modifiers;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link ILootModifier} that modifies the entries of a {@link LootPool} in a {@link LootTable}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootPoolEntriesModifier implements ILootModifier<LootPoolEntriesModifier.Config> {
	public static final Field ENTRIES = ObfuscationReflectionHelper.findField(LootPool.class, "entries");

	@SuppressWarnings("unchecked")
	@Override
	public void modify(LootTableLoadEvent object, Config config) {
		try {
			List<LootEntry> lootEntries = (List<LootEntry>) ENTRIES.get(((List<LootPool>) LootPoolsModifier.POOLS.get(object.getTable())).get(config.index));
			if (config.replace) {
				lootEntries.clear();
			}
			lootEntries.addAll(config.entries);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement serialize(Config config, Gson gson) throws JsonParseException {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("replace", config.replace);
		jsonObject.addProperty("index", config.index);
		JsonArray entries = new JsonArray();
		for (LootEntry lootEntry : config.entries) {
			entries.add(gson.toJsonTree(lootEntry));
		}
		jsonObject.add("entries", entries);
		return jsonObject;
	}

	@Override
	public Config deserialize(JsonElement element, Pair<Gson, LootPredicateManager> additional) throws JsonParseException {
		JsonObject jsonObject = element.getAsJsonObject();
		int index = JSONUtils.getAsInt(jsonObject, "index");
		if (index < 0) {
			throw new JsonParseException("'index' must be 0 or greater!");
		}
		List<LootEntry> entries = new ArrayList<>();
		JsonArray entriesArray = jsonObject.getAsJsonArray("entries");
		Gson gson = additional.getFirst();
		entriesArray.forEach(entry -> entries.add(gson.fromJson(entry, LootEntry.class)));
		return new Config(JSONUtils.getAsBoolean(jsonObject, "replace"), index, entries);
	}

	public static class Config {
		private final boolean replace;
		private final int index;
		private final List<LootEntry> entries;

		public Config(boolean replace, int index, List<LootEntry> entries) {
			this.replace = replace;
			this.index = index;
			this.entries = entries;
		}
	}
}
