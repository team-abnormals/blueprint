package com.teamabnormals.blueprint.core.util.modification.targeting.selectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.targeting.ConditionedModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.ConfiguredModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.ModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ModifierTargetSelector} implementation that acts as multiple {@link ConditionedModifierTargetSelector} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class MultiModifierTargetSelector implements ModifierTargetSelector<List<ConditionedModifierTargetSelector<?, ?>>> {

	@Override
	public List<ResourceLocation> getTargetNames(SelectionSpace space, List<ConditionedModifierTargetSelector<?, ?>> config) {
		List<ResourceLocation> targetNames = new ArrayList<>();
		config.forEach(configuredModifierTargetSelector -> targetNames.addAll(configuredModifierTargetSelector.getTargetNames(space)));
		return targetNames;
	}

	@Override
	public JsonElement serialize(List<ConditionedModifierTargetSelector<?, ?>> config) {
		JsonArray jsonArray = new JsonArray();
		config.forEach(selector -> jsonArray.add(selector.serialize()));
		return jsonArray;
	}

	@Override
	public List<ConditionedModifierTargetSelector<?, ?>> deserialize(JsonElement jsonElement) {
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		List<ConditionedModifierTargetSelector<?, ?>> targetSelectors = new ArrayList<>(jsonArray.size());
		jsonArray.forEach(element -> targetSelectors.add(new ConditionedModifierTargetSelector<>(ConfiguredModifierTargetSelector.deserialize(element.getAsJsonObject()))));
		return targetSelectors;
	}

}
