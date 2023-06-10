package com.teamabnormals.blueprint.common.loot.modification.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.LootModifierSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

/**
 * A {@link LootModifier} implementation that modifies the {@link LootContextParamSet} of a {@link LootTable}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record LootTypeModifier(LootContextParamSet lootContextParamSet) implements LootModifier<LootTypeModifier> {
	private static final Field PARAMETER_SET = ObfuscationReflectionHelper.findField(LootTable.class, "f_79108_");

	@Override
	public void modify(LootTableLoadEvent event) {
		try {
			PARAMETER_SET.set(event.getTable(), this.lootContextParamSet);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Serializer getSerializer() {
		return LootModifierSerializers.TYPE;
	}

	public static final class Serializer implements LootModifier.Serializer<LootTypeModifier> {
		@Override
		public JsonElement serialize(LootTypeModifier modifier, Gson additional) throws JsonParseException {
			LootContextParamSet lootContextParamSet = modifier.lootContextParamSet;
			ResourceLocation resourceLocation = LootContextParamSets.getKey(lootContextParamSet);
			if (resourceLocation == null) throw new JsonParseException("Unknown Loot Parameter Set: " + lootContextParamSet);
			return new JsonPrimitive(resourceLocation.toString());
		}

		@Override
		public LootTypeModifier deserialize(JsonElement element, Pair<Gson, LootDataManager> additional) throws JsonParseException {
			String type = element.getAsString();
			LootContextParamSet lootParameterSet = LootContextParamSets.get(new ResourceLocation(type));
			if (lootParameterSet != null) return new LootTypeModifier(lootParameterSet);
			throw new JsonParseException("Unknown type: " + type);
		}
	}
}
