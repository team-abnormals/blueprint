package com.teamabnormals.blueprint.common.loot.modification;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraftforge.event.LootTableLoadEvent;

import java.util.concurrent.CompletableFuture;

public abstract class LootModifierProvider extends ObjectModifierProvider<LootTableLoadEvent, Gson, Pair<Gson, LootDataManager>> {

	public LootModifierProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(modid, true, LootModificationManager.TARGET_DIRECTORY, LootModifierSerializers.REGISTRY, Deserializers.createLootTableSerializer().create(), output, lookupProvider);
	}

}