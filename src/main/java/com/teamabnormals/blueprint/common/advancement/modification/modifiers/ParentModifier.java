package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;

/**
 * An {@link IAdvancementModifier} implementation that modifies the parent advancement of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ParentModifier implements IAdvancementModifier<ResourceLocation> {

	@Override
	public void modify(Advancement.Builder builder, ResourceLocation location) {
		builder.parent(location);
	}

	@Override
	public JsonElement serialize(ResourceLocation config, Void additional) throws JsonParseException {
		return new JsonPrimitive(config.toString());
	}

	@Override
	public ResourceLocation deserialize(JsonElement element, DeserializationContext additional) throws JsonParseException {
		return new ResourceLocation(element.getAsString());
	}

}
