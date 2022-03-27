package com.teamabnormals.blueprint.common.advancement.modification;

import com.teamabnormals.blueprint.core.util.modification.ObjectModifierProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.data.DataGenerator;

public abstract class AdvancementModifierProvider extends ObjectModifierProvider<Advancement.Builder, Void, DeserializationContext> {

	public AdvancementModifierProvider(DataGenerator dataGenerator, String modid) {
		super(dataGenerator, modid, true, AdvancementModificationManager.TARGET_PATH, AdvancementModifierSerializers.REGISTRY, null);
	}

}