package com.teamabnormals.blueprint.common.advancement.modification;

import com.teamabnormals.blueprint.core.util.modification.ObjectModifierProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class AdvancementModifierProvider extends ObjectModifierProvider<Advancement.Builder, Void, DeserializationContext> {

	public AdvancementModifierProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(modid, true, AdvancementModificationManager.TARGET_PATH, AdvancementModifierSerializers.REGISTRY, (Void) null, output, lookupProvider);
	}

}