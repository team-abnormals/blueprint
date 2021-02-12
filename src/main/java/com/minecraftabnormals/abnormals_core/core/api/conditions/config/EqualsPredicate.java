package com.minecraftabnormals.abnormals_core.core.api.conditions.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public class EqualsPredicate implements IConfigPredicate {
    private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "equals");
    private final Object value;

    public EqualsPredicate(Object value) {
        this.value = value;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(ForgeConfigSpec.ConfigValue<?> toCompare) {
        return toCompare.get().equals(value);
    }

    public static class Serializer implements IConfigPredicateSerializer<EqualsPredicate> {
        private static final ResourceLocation ID = new ResourceLocation(AbnormalsCore.MODID, "equals");

        @Override
        public void write(JsonObject json, IConfigPredicate value) {
            if (!(value instanceof EqualsPredicate)) throw new IllegalArgumentException("Incompatible predicate type");
            Object object = ((EqualsPredicate) value).value;
            if (object == null) {
                json.add("value", JsonNull.INSTANCE);
            } else if (object instanceof String) {
                json.addProperty("value", (String) object);
            } else if (object instanceof Number) {
                json.addProperty("value", (Number) object);
            } else if (object instanceof Boolean) {
                json.addProperty("value", (Boolean) object);
            } else throw new IllegalArgumentException("Predicate value cannot be serialized");
        }

        @Override
        public EqualsPredicate read(JsonObject json) {
            if (!json.has("value"))
                throw new JsonSyntaxException("Missing 'value', expected to find a object");
            Object value;
            JsonElement element = json.get("value");
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    value = primitive.getAsDouble();
                } else if (primitive.isBoolean()) {
                    value = primitive.getAsBoolean();
                } else value = primitive.getAsString();
            } else if (element.isJsonNull()) {
                value = null;
            } else throw new JsonSyntaxException("Cannot deserialize field, not a recognizable type");
            return new EqualsPredicate(value);
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
