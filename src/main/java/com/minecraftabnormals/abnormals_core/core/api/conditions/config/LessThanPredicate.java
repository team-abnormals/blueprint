package com.minecraftabnormals.abnormals_core.core.api.conditions.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A config predicate that checks whether the number got from a {@link ForgeConfigSpec.ConfigValue} instance is less
 * than a stored number. Throws an exception if the config value type is not an instance of {@code Number}.
 *
 * @author abigailfails
 */
public class LessThanPredicate implements IConfigPredicate {
    private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "less_than");
    private final double value;

    public LessThanPredicate(double value) {
        this.value = value;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(ForgeConfigSpec.ConfigValue<?> toCompare) {
        try {
            return ((Number) toCompare.get()).doubleValue() < value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid config value type; must hold a number");
        }
    }

    public static class Serializer implements IConfigPredicateSerializer<LessThanPredicate> {
        private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "less_than");

        @Override
        public void write(JsonObject json, IConfigPredicate value) {
            if (!(value instanceof LessThanPredicate)) throw new IllegalArgumentException("Incompatible predicate type");
            json.addProperty("value", ((LessThanPredicate) value).value);
        }

        @Override
        public LessThanPredicate read(JsonObject json) {
            if (!json.has("value"))
                throw new JsonSyntaxException("Missing 'value', expected to find a number");
            try {
                return new LessThanPredicate(json.get("value").getAsDouble());
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
