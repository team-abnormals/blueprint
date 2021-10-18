package com.teamabnormals.blueprint.core.api.conditions.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A config predicate that checks whether the string got from a {@link ForgeConfigSpec.ConfigValue} instance contains a stored string.
 * <p>Throws an exception if the config value type is not {@code String}.</p>
 *
 * @author abigailfails
 */
public class ContainsPredicate implements IConfigPredicate {
	private static final ResourceLocation ID = new ResourceLocation(Blueprint.MOD_ID, "contains");
	private final String value;

	public ContainsPredicate(String value) {
		this.value = value;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(ForgeConfigSpec.ConfigValue<?> toCompare) {
		if (toCompare.get() instanceof String) {
			return ((String) toCompare.get()).matches(value);
		}
		throw new IllegalArgumentException("Invalid config value type; must hold a String");
	}

	public static class Serializer implements IConfigPredicateSerializer<ContainsPredicate> {
		private static final ResourceLocation ID = new ResourceLocation(Blueprint.MOD_ID, "contains");

		@Override
		public void write(JsonObject json, IConfigPredicate value) {
			if (!(value instanceof ContainsPredicate))
				throw new IllegalArgumentException("Incompatible predicate type");
			json.addProperty("value", ((ContainsPredicate) value).value);
		}

		@Override
		public ContainsPredicate read(JsonObject json) {
			if (!json.has("value") && !GsonHelper.isStringValue(json, "value"))
				throw new JsonSyntaxException("Missing 'value', expected to find a string");
			return new ContainsPredicate(json.get("value").getAsString());
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	}
}
