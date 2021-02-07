package com.minecraftabnormals.abnormals_core.core.api.conditions.config_predicates.comparison;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config_predicates.IConfigPredicate;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config_predicates.IConfigPredicateSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.math.BigDecimal;

public class LessThanOrEqualPredicate implements IConfigPredicate {
    private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "less_than_or_equal_to");
    private final BigDecimal value;

    public LessThanOrEqualPredicate(BigDecimal value) {
        this.value = value;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(ForgeConfigSpec.ConfigValue<?> toCompare) {
        try {
            BigDecimal number = new BigDecimal(toCompare.get().toString());
            return number.compareTo(value) <= 0;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid config value type; must hold a Number");
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
                return new LessThanOrEqualPredicate(json.get("value").getAsBigDecimal());
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException("'value' does not contain a number");
            }
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
