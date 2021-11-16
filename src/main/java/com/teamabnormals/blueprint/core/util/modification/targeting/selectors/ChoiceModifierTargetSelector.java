package com.teamabnormals.blueprint.core.util.modification.targeting.selectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teamabnormals.blueprint.core.util.modification.targeting.ConditionedModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.ConfiguredModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.ModifierTargetSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link ModifierTargetSelector} implementation that picks a {@link ConditionedModifierTargetSelector} if a condition is met or picks another {@link ConditionedModifierTargetSelector} if the condition is not met.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ChoiceModifierTargetSelector implements ModifierTargetSelector<ChoiceModifierTargetSelector.Config> {

	@Override
	public List<ResourceLocation> getTargetNames(Set<Map.Entry<ResourceLocation, JsonElement>> resources, Config config) {
		return config.condition.test() ? config.first.getTargetNames(resources) : config.second.getTargetNames(resources);
	}

	@Override
	public JsonElement serialize(Config config) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("first", config.first.serialize());
		jsonObject.add("second", config.second.serialize());
		jsonObject.add("condition", CraftingHelper.serialize(config.condition));
		return jsonObject;
	}

	@Override
	public Config deserialize(JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonObject conditionObject = GsonHelper.convertToJsonObject(jsonObject.get("condition"), "condition");
		ICondition condition;
		try {
			condition = CraftingHelper.getCondition(conditionObject);
		} catch (JsonSyntaxException e) {
			//Support for conditions that may not exist under certain circumstances
			return new Config(new ConditionedModifierTargetSelector<>(ConfiguredModifierTargetSelector.EMPTY), new ConditionedModifierTargetSelector<>(ConfiguredModifierTargetSelector.deserialize(GsonHelper.convertToJsonObject(jsonObject.get("second"), "second"))), FalseCondition.INSTANCE);
		}
		return new Config(new ConditionedModifierTargetSelector<>(ConfiguredModifierTargetSelector.deserialize(GsonHelper.convertToJsonObject(jsonObject.get("first"), "first"))), new ConditionedModifierTargetSelector<>(ConfiguredModifierTargetSelector.deserialize(GsonHelper.convertToJsonObject(jsonObject.get("second"), "second"))), condition);
	}

	/**
	 * The record class for the configurable properties of the {@link ChoiceModifierTargetSelector}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static record Config(ConditionedModifierTargetSelector<?, ?> first, ConditionedModifierTargetSelector<?, ?> second, ICondition condition) {
	}

}
