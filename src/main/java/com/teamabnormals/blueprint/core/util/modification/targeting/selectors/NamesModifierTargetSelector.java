package com.teamabnormals.blueprint.core.util.modification.targeting.selectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.targeting.ModifierTargetSelector;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link ModifierTargetSelector} implementation that returns a configurable list of target names.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class NamesModifierTargetSelector implements ModifierTargetSelector<List<ResourceLocation>> {

	@Override
	public List<ResourceLocation> getTargetNames(Set<Map.Entry<ResourceLocation, JsonElement>> resources, List<ResourceLocation> config) {
		return config;
	}

	@Override
	public JsonElement serialize(List<ResourceLocation> config) {
		JsonArray jsonArray = new JsonArray();
		config.forEach(location -> jsonArray.add(location.toString()));
		return jsonArray;
	}

	@Override
	public List<ResourceLocation> deserialize(JsonElement jsonElement) {
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		List<ResourceLocation> locations = new ArrayList<>();
		jsonArray.forEach(element -> locations.add(new ResourceLocation(element.getAsString())));
		return locations;
	}

}
