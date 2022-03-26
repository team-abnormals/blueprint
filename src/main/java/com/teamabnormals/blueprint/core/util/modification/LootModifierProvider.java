package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModificationManager;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import com.teamabnormals.blueprint.common.loot.modification.LootModificationManager;
import com.teamabnormals.blueprint.common.loot.modification.LootModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.event.LootTableLoadEvent;

public class LootModifierProvider extends ObjectModifierProvider<LootTableLoadEvent, Gson, Pair<Gson, PredicateManager>> {

	public LootModifierProvider(DataGenerator dataGenerator, String modid) {
		super(dataGenerator, modid, true, LootModificationManager.TARGET_DIRECTORY, new GsonBuilder().setPrettyPrinting().create(), LootModifierSerializers.REGISTRY, (group) -> null);
	}

	@Override
	protected void registerEntries() {

	}
}