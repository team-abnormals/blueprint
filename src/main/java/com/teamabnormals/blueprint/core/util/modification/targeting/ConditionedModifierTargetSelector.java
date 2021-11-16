package com.teamabnormals.blueprint.core.util.modification.targeting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class that represents a {@link ConfiguredModifierTargetSelector} with conditions.
 *
 * @param <C> The type of config for the type of {@link ModifierTargetSelector}.
 * @param <S> The type of {@link ModifierTargetSelector} to configure for.
 * @author SmellyModder (Luke Tonon)
 */
public final class ConditionedModifierTargetSelector<C, S extends ModifierTargetSelector<C>> {
	private static final ICondition[] NO_CONDITIONS = new ICondition[0];
	private final ConfiguredModifierTargetSelector<C, S> targetSelector;
	private final ICondition[] conditions;

	public ConditionedModifierTargetSelector(ConfiguredModifierTargetSelector<C, S> targetSelector, ICondition[] conditions) {
		this.targetSelector = targetSelector;
		this.conditions = conditions;
	}

	public ConditionedModifierTargetSelector(ConfiguredModifierTargetSelector<C, S> targetSelector) {
		this(targetSelector, NO_CONDITIONS);
	}

	/**
	 * Serializes this {@link ConditionedModifierTargetSelector} as a {@link JsonObject} instance.
	 *
	 * @return A {@link JsonObject} representation of this {@link ConditionedModifierTargetSelector}.
	 */
	public JsonObject serialize() {
		JsonObject jsonObject = this.targetSelector.serialize();
		JsonArray conditions = new JsonArray();
		for (ICondition condition : this.conditions) {
			conditions.add(CraftingHelper.serialize(condition));
		}
		jsonObject.add("conditions", conditions);
		return jsonObject;
	}

	/**
	 * Gets a list of {@link ResourceLocation} names to target from a set of resources.
	 *
	 * @param resources A set of resources to use for getting the target names.
	 * @return A list of {@link ResourceLocation} names to target from a set of resources.
	 */
	public List<ResourceLocation> getTargetNames(Set<Map.Entry<ResourceLocation, JsonElement>> resources) {
		return this.targetSelector.getTargetNames(resources);
	}

	/**
	 * Gets the {@link #targetSelector}.
	 *
	 * @return The {@link #targetSelector}.
	 */
	public ConfiguredModifierTargetSelector<C, S> getTargetSelector() {
		return this.targetSelector;
	}

	/**
	 * Gets the array of {@link #conditions}.
	 *
	 * @return The array of {@link #conditions}.
	 */
	public ICondition[] getConditions() {
		return this.conditions;
	}
}
