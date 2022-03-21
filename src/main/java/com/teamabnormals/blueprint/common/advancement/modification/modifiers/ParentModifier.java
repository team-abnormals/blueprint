package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;

/**
 * An {@link AdvancementModifier} implementation that modifies the parent advancement of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ParentModifier(ResourceLocation parent) implements AdvancementModifier<ParentModifier> {
	@Override
	public void modify(Advancement.Builder builder) {
		builder.parent(this.parent);
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.PARENT;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<ParentModifier> {
		@Override
		public JsonElement serialize(ParentModifier modifier, Void additional) throws JsonParseException {
			return new JsonPrimitive(modifier.parent.toString());
		}

		@Override
		public ParentModifier deserialize(JsonElement element, DeserializationContext additional) throws JsonParseException {
			return new ParentModifier(new ResourceLocation(element.getAsString()));
		}
	}
}
