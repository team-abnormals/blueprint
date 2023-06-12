package com.teamabnormals.blueprint.common.loot.modification.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.LootModifierSerializers;
import com.teamabnormals.blueprint.core.util.DataUtil;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LootModifier} implementation that modifies the entries of a {@link LootPool} in a {@link net.minecraft.world.level.storage.loot.LootTable}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record LootPoolEntriesModifier(boolean replace, int index, List<LootPoolEntryContainer> entries) implements LootModifier<LootPoolEntriesModifier> {
	public static final Field ENTRIES = ObfuscationReflectionHelper.findField(LootPool.class, "f_79023_");

	public LootPoolEntriesModifier(boolean replace, int index, LootPoolEntryContainer... entries) {
		this(replace, index, List.of(entries));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modify(LootTable object) {
		try {
			LootPool pool = ((List<LootPool>) LootPoolsModifier.POOLS.get(object)).get(this.index);
			LootPoolEntryContainer[] lootEntries = (LootPoolEntryContainer[]) ENTRIES.get(pool);
			if (this.replace) {
				lootEntries = this.entries.toArray(LootPoolEntryContainer[]::new);
			} else {
				lootEntries = DataUtil.concatArrays(lootEntries, this.entries.toArray(LootPoolEntryContainer[]::new));
			}
			ENTRIES.set(pool, lootEntries);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Serializer getSerializer() {
		return LootModifierSerializers.ENTRIES;
	}

	public static final class Serializer implements LootModifier.Serializer<LootPoolEntriesModifier> {
		@Override
		public JsonElement serialize(LootPoolEntriesModifier modifier, Gson gson) throws JsonParseException {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("replace", modifier.replace);
			jsonObject.addProperty("index", modifier.index);
			JsonArray entries = new JsonArray();
			for (LootPoolEntryContainer lootEntry : modifier.entries) {
				entries.add(gson.toJsonTree(lootEntry));
			}
			jsonObject.add("entries", entries);
			return jsonObject;
		}

		@Override
		public LootPoolEntriesModifier deserialize(JsonElement element, Pair<Gson, LootDataManager> additional) throws JsonParseException {
			JsonObject jsonObject = element.getAsJsonObject();
			int index = GsonHelper.getAsInt(jsonObject, "index");
			if (index < 0) throw new JsonParseException("'index' must be 0 or greater!");
			List<LootPoolEntryContainer> entries = new ArrayList<>();
			JsonArray entriesArray = jsonObject.getAsJsonArray("entries");
			Gson gson = additional.getFirst();
			entriesArray.forEach(entry -> entries.add(gson.fromJson(entry, LootPoolEntryContainer.class)));
			return new LootPoolEntriesModifier(GsonHelper.getAsBoolean(jsonObject, "replace"), index, entries);
		}
	}
}
