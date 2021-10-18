package com.teamabnormals.blueprint.common.advancement.modification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.*;
import com.teamabnormals.blueprint.core.util.modification.ModifierDataProvider;
import com.teamabnormals.blueprint.core.util.modification.ModifierRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.data.DataGenerator;

/**
 * The registry class for {@link IAdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class AdvancementModifiers {
	public static final ModifierRegistry<Builder, Void, DeserializationContext> REGISTRY = new ModifierRegistry<>();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static final ParentModifier PARENT_MODIFIER = REGISTRY.register("parent", new ParentModifier());
	public static final RewardsModifier REWARDS_MODIFIER = REGISTRY.register("rewards", new RewardsModifier());
	public static final DisplayInfoModifier DISPLAY_INFO_MODIFIER = REGISTRY.register("display", new DisplayInfoModifier());
	public static final CriteriaModifier CRITERIA_MODIFIER = REGISTRY.register("criteria", new CriteriaModifier());
	public static final IndexedRequirementsModifier INDEXED_REQUIREMENTS_MODIFIER = REGISTRY.register("indexed_requirements", new IndexedRequirementsModifier());
	public static final EffectsChangedModifier EFFECTS_CHANGED_MODIFIER = REGISTRY.register("effects_changed", new EffectsChangedModifier());

	/**
	 * Creates a new {@link ModifierDataProvider} for {@link IAdvancementModifier}s.
	 *
	 * @param dataGenerator A {@link DataGenerator} to use when generating the configured {@link IAdvancementModifier}s.
	 * @param name          A name for the provider.
	 * @param modId         The ID of the mod using this provider.
	 * @param toGenerate    An array of {@link ModifierDataProvider.ProviderEntry}s to generate.
	 * @return A new {@link ModifierDataProvider} for {@link IAdvancementModifier}s.
	 */
	@SafeVarargs
	public static ModifierDataProvider<Advancement.Builder, Void, DeserializationContext> createDataProvider(DataGenerator dataGenerator, String name, String modId, ModifierDataProvider.ProviderEntry<Advancement.Builder, Void, DeserializationContext>... toGenerate) {
		return new ModifierDataProvider<>(dataGenerator, name, GSON, (path, tsdProviderEntry) -> path.resolve("data/" + modId + "/modifiers/advancements/" + tsdProviderEntry.name.getPath() + ".json"), "advancement", REGISTRY, builderVoidDeserializationContextTargetedModifier -> null, toGenerate);
	}
}
