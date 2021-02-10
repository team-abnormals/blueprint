package com.minecraftabnormals.abnormals_core.core.api.conditions.config_predicates;

import com.google.gson.JsonObject;
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
            if (object instanceof String) { //TODO less hardcoding
                json.addProperty("value", (String) object);
            } else if (object instanceof Number) {
                json.addProperty("value", (Number) object);

            } else if (object instanceof Boolean) {
                json.addProperty("value", (Boolean) object);
            } else if (object instanceof Character) {
                json.addProperty("value", (Character) object);
            } else throw new IllegalArgumentException("Predicate value cannot be serialized");
        }

        @Override
        public EqualsPredicate read(JsonObject json) {
            if (!json.has("value"))
                throw new JsonSyntaxException("Missing 'value', expected to find a object");
            return new EqualsPredicate(json.get("value"));
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
