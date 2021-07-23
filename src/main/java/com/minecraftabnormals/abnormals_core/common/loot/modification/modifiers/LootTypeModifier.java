package com.minecraftabnormals.abnormals_core.common.loot.modification.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

/**
 * An {@link ILootModifier} that modifies the {@link LootParameterSet} of a {@link LootTable}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootTypeModifier implements ILootModifier<LootParameterSet> {
	private static final Field PARAMETER_SET = ObfuscationReflectionHelper.findField(LootTable.class, "field_216127_d");

	@Override
	public void modify(LootTableLoadEvent event, LootParameterSet config) {
		try {
			PARAMETER_SET.set(event.getTable(), config);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement serialize(LootParameterSet config, Gson additional) throws JsonParseException {
		ResourceLocation resourceLocation = LootParameterSets.getKey(config);
		if (resourceLocation == null) {
			throw new JsonParseException("Unknown Loot Parameter Set: " + config);
		}
		return new JsonPrimitive(resourceLocation.toString());
	}

	@Override
	public LootParameterSet deserialize(JsonElement element, Pair<Gson, LootPredicateManager> additional) throws JsonParseException {
		String type = element.getAsString();
		LootParameterSet lootParameterSet = LootParameterSets.get(new ResourceLocation(type));
		if (lootParameterSet != null) {
			return lootParameterSet;
		}
		throw new JsonParseException("Unknown type: " + type);
	}
}
