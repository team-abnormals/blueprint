package com.teamabnormals.blueprint.core.util.modification.targeting.selectors;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Unit;
import com.teamabnormals.blueprint.core.util.modification.targeting.ModifierTargetSelector;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link ModifierTargetSelector} implementation that always returns an empty list of target names.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class EmptyModifierTargetSelector implements ModifierTargetSelector<Unit> {
	private static final ImmutableList<ResourceLocation> EMPTY = ImmutableList.of();

	@Override
	public List<ResourceLocation> getTargetNames(Set<Map.Entry<ResourceLocation, JsonElement>> resources, Unit config) {
		return EMPTY;
	}

	@Override
	public JsonElement serialize(Unit config) {
		return new JsonObject();
	}

	@Override
	public Unit deserialize(JsonElement jsonElement) {
		return Unit.INSTANCE;
	}
}
