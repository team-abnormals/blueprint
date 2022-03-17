package com.teamabnormals.blueprint.common.advancement.modification;

import com.teamabnormals.blueprint.common.advancement.modification.modifiers.*;
import com.teamabnormals.blueprint.core.util.modification.ModifierRegistry;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.DeserializationContext;

/**
 * The registry class for {@link IAdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class AdvancementModifiers {
	public static final ModifierRegistry<Builder, Void, DeserializationContext> REGISTRY = new ModifierRegistry<>();

	public static final ParentModifier PARENT_MODIFIER = REGISTRY.register("parent", new ParentModifier());
	public static final RewardsModifier REWARDS_MODIFIER = REGISTRY.register("rewards", new RewardsModifier());
	public static final DisplayInfoModifier DISPLAY_INFO_MODIFIER = REGISTRY.register("display", new DisplayInfoModifier());
	public static final CriteriaModifier CRITERIA_MODIFIER = REGISTRY.register("criteria", new CriteriaModifier());
	public static final IndexedRequirementsModifier INDEXED_REQUIREMENTS_MODIFIER = REGISTRY.register("indexed_requirements", new IndexedRequirementsModifier());
	public static final EffectsChangedModifier EFFECTS_CHANGED_MODIFIER = REGISTRY.register("effects_changed", new EffectsChangedModifier());
}
