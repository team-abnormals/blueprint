package com.minecraftabnormals.abnormals_core.core.api.conditions.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A config predicate that checks whether the number got from a {@link ForgeConfigSpec.ConfigValue} instance is less
 * than or equal to a stored number. Throws an exception if the config value type is not an instance of {@code Number}.
 *
 * @author abigailfails
 */
public class LessThanOrEqualPredicate implements IConfigPredicate {
    private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "less_than_or_equal_to");
    private final double value;

    public LessThanOrEqualPredicate(double value) {
        this.value = value;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(ForgeConfigSpec.ConfigValue<?> toCompare) {
        try {
            return ((Number) toCompare.get()).doubleValue() <= value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid config value type; must hold a number");
        }
    }

    public static class Serializer implements IConfigPredicateSerializer<LessThanOrEqualPredicate> {
        private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "less_than_or_equal_to");

        @Override
        public void write(JsonObject json, IConfigPredicate value) {
            if (!(value instanceof LessThanOrEqualPredicate)) throw new IllegalArgumentException("Incompatible predicate type");
            json.addProperty("value", ((LessThanOrEqualPredicate) value).value);
        }

        @Override
        public LessThanOrEqualPredicate read(JsonObject json) {
            if (!json.has("value"))
                throw new JsonSyntaxException("Missing 'value', expected to find a number");
            try {
                return new LessThanOrEqualPredicate(json.get("value").getAsDouble());
            } catch (ClassCastException|IllegalStateException e) {
                throw new JsonSyntaxException("'value' does not contain a number");
            }
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
