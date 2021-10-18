package com.teamabnormals.blueprint.core.api.conditions.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A config predicate that checks whether the string got from a {@link ForgeConfigSpec.ConfigValue} instance matches a stored regular expression.
 * <p>Throws an exception if the config value type is not {@code String}.</p>
 *
 * @author abigailfails
 */
public class MatchesPredicate implements IConfigPredicate {
	private static final ResourceLocation ID = new ResourceLocation(Blueprint.MOD_ID, "matches");
	private final String regex;

	public MatchesPredicate(String regex) {
		this.regex = regex;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(ForgeConfigSpec.ConfigValue<?> toCompare) {
		if (toCompare.get() instanceof String) {
			return ((String) toCompare.get()).matches(regex);
		}
		throw new IllegalArgumentException("Invalid config value type; must hold a String");
	}

	public static class Serializer implements IConfigPredicateSerializer<MatchesPredicate> {
		private static final ResourceLocation ID = new ResourceLocation(Blueprint.MOD_ID, "matches");

		@Override
		public void write(JsonObject json, IConfigPredicate value) {
			if (!(value instanceof MatchesPredicate)) throw new IllegalArgumentException("Incompatible predicate type");
			json.addProperty("expression", ((MatchesPredicate) value).regex);
		}

		@Override
		public MatchesPredicate read(JsonObject json) {
			if (!json.has("expression") && !GsonHelper.isStringValue(json, "expression"))
				throw new JsonSyntaxException("Missing 'expression', expected to find a regular expression");
			return new MatchesPredicate(json.get("expression").getAsString());
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	}
}
