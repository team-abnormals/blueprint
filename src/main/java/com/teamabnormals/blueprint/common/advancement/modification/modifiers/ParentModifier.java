package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import com.teamabnormals.blueprint.common.advancement.modification.BlueprintAdvancementBuilder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

/**
 * An {@link AdvancementModifier} implementation that modifies the parent advancement of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ParentModifier(ResourceLocation parent) implements AdvancementModifier<ParentModifier> {
	@Override
	public void modify(BlueprintAdvancementBuilder builder) {
		builder.parent(this.parent);
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.PARENT;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<ParentModifier> {
		@Override
		public JsonElement serialize(ParentModifier modifier, RegistryOps<JsonElement> ops) throws JsonParseException {
			return new JsonPrimitive(modifier.parent.toString());
		}

		@Override
		public ParentModifier deserialize(JsonElement element, RegistryOps<JsonElement> ops) throws JsonParseException {
			return new ParentModifier(ResourceLocation.parse(element.getAsString()));
		}
	}
}
