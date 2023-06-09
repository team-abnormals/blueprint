package com.teamabnormals.blueprint.common.loot.modification;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraftforge.event.LootTableLoadEvent;

public abstract class LootModifierProvider extends ObjectModifierProvider<LootTableLoadEvent, Gson, Pair<Gson, LootDataManager>> {

	public LootModifierProvider(DataGenerator dataGenerator, String modid) {
		super(dataGenerator, modid, true, LootModificationManager.TARGET_DIRECTORY, LootModifierSerializers.REGISTRY, Deserializers.createLootTableSerializer().create());
	}

}