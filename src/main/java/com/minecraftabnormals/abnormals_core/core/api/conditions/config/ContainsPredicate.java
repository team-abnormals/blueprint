package com.minecraftabnormals.abnormals_core.core.api.conditions.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public class ContainsPredicate implements IConfigPredicate {
    private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "contains");
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
        private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "contains");

        @Override
        public void write(JsonObject json, IConfigPredicate value) {
            if (!(value instanceof ContainsPredicate)) throw new IllegalArgumentException("Incompatible predicate type");
            json.addProperty("value", ((ContainsPredicate) value).value);
        }

        @Override
        public ContainsPredicate read(JsonObject json) {
            if (!json.has("value") && !JSONUtils.isString(json, "value"))
                throw new JsonSyntaxException("Missing 'value', expected to find a string");
            return new ContainsPredicate(json.get("value").getAsString());
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
