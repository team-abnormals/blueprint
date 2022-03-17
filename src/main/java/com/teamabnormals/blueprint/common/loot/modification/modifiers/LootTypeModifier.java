package com.teamabnormals.blueprint.common.loot.modification.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

/**
 * An {@link ILootModifier} that modifies the {@link LootContextParamSet} of a {@link LootTable}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootTypeModifier implements ILootModifier<LootContextParamSet> {
	private static final Field PARAMETER_SET = ObfuscationReflectionHelper.findField(LootTable.class, "f_79108_");

	@Override
	public void modify(LootTableLoadEvent event, LootContextParamSet config) {
		try {
			PARAMETER_SET.set(event.getTable(), config);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement serialize(LootContextParamSet config, Gson additional) throws JsonParseException {
		ResourceLocation resourceLocation = LootContextParamSets.getKey(config);
		if (resourceLocation == null) {
			throw new JsonParseException("Unknown Loot Parameter Set: " + config);
		}
		return new JsonPrimitive(resourceLocation.toString());
	}

	@Override
	public LootContextParamSet deserialize(JsonElement element, Pair<Gson, PredicateManager> additional) throws JsonParseException {
		String type = element.getAsString();
		LootContextParamSet lootParameterSet = LootContextParamSets.get(new ResourceLocation(type));
		if (lootParameterSet != null) {
			return lootParameterSet;
		}
		throw new JsonParseException("Unknown type: " + type);
	}
}
