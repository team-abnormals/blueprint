package com.teamabnormals.blueprint.core.api.conditions.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A config predicate that checks whether the number got from a {@link ForgeConfigSpec.ConfigValue} instance is greater than or equal to a stored number.
 * <p>Throws an exception if the config value type is not an instance of {@code Number}.</p>
 *
 * @author abigailfails
 */
public class GreaterThanOrEqualPredicate implements IConfigPredicate {
	private static final ResourceLocation ID = new ResourceLocation(Blueprint.MOD_ID, "greater_than_or_equal_to");
	private final double value;

	public GreaterThanOrEqualPredicate(double value) {
		this.value = value;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(ForgeConfigSpec.ConfigValue<?> toCompare) {
		try {
			return ((Number) toCompare.get()).doubleValue() >= value;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Invalid config value type; must hold a number");
		}
	}

	public static class Serializer implements IConfigPredicateSerializer<GreaterThanOrEqualPredicate> {
		private static final ResourceLocation ID = new ResourceLocation(Blueprint.MOD_ID, "greater_than_or_equal_to");

		@Override
		public void write(JsonObject json, IConfigPredicate value) {
			if (!(value instanceof GreaterThanOrEqualPredicate))
				throw new IllegalArgumentException("Incompatible predicate type");
			json.addProperty("value", ((GreaterThanOrEqualPredicate) value).value);
		}

		@Override
		public GreaterThanOrEqualPredicate read(JsonObject json) {
			if (!json.has("value"))
				throw new JsonSyntaxException("Missing 'value', expected to find a number");
			try {
				return new GreaterThanOrEqualPredicate(json.get("value").getAsDouble());
			} catch (ClassCastException | IllegalStateException e) {
				throw new JsonSyntaxException("'value' does not contain a number");
			}
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	}
}
