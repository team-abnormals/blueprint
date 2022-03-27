package com.teamabnormals.blueprint.common.advancement.modification;

import com.teamabnormals.blueprint.common.advancement.modification.modifiers.*;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierSerializerRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;

/**
 * The registry class for {@link AdvancementModifier.Serializer} types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class AdvancementModifierSerializers {
	public static final ObjectModifierSerializerRegistry<Advancement.Builder, Void, DeserializationContext> REGISTRY = new ObjectModifierSerializerRegistry<>();

	public static final ParentModifier.Serializer PARENT = REGISTRY.register("parent", new ParentModifier.Serializer());
	public static final RewardsModifier.Serializer REWARDS = REGISTRY.register("rewards", new RewardsModifier.Serializer());
	public static final DisplayInfoModifier.Serializer DISPLAY_INFO = REGISTRY.register("display", new DisplayInfoModifier.Serializer());
	public static final CriteriaModifier.Serializer CRITERIA = REGISTRY.register("criteria", new CriteriaModifier.Serializer());
	public static final EffectsChangedModifier.Serializer EFFECTS_CHANGED = REGISTRY.register("effects_changed", new EffectsChangedModifier.Serializer());
}
