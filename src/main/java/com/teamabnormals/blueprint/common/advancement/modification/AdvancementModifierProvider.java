package com.teamabnormals.blueprint.common.advancement.modification;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;

import java.util.concurrent.CompletableFuture;

public abstract class AdvancementModifierProvider extends ObjectModifierProvider<Advancement.Builder, RegistryOps<JsonElement>, RegistryOps<JsonElement>> {

	public AdvancementModifierProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(modid, true, AdvancementModificationManager.TARGET_PATH, AdvancementModifierSerializers.REGISTRY, (registryOps, group) -> registryOps, output, lookupProvider);
	}

}