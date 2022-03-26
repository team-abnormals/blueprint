package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.GsonBuilder;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModificationManager;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.data.DataGenerator;

public class AdvancementModifierProvider extends ObjectModifierProvider<Advancement.Builder, Void, DeserializationContext> {

	public AdvancementModifierProvider(DataGenerator dataGenerator, String modid) {
		super(dataGenerator, modid, true, AdvancementModificationManager.TARGET_PATH, new GsonBuilder().setPrettyPrinting().create(), AdvancementModifierSerializers.REGISTRY, (group) -> null);
	}

	@Override
	protected void registerEntries() {

	}
}