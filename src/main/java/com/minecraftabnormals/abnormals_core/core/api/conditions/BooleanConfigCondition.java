package com.minecraftabnormals.abnormals_core.core.api.conditions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.util.DataUtil;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;

/**
 * A condition that checks against the values of boolean config fields. The easiest way to register is through
 * {@link DataUtil#registerBooleanConfigCondition(String, boolean, Object...)} which automatically handles mapping
 * config fields to JSON string arguments.
 *
 * <p>Takes the arguments:
 * <ul>
 *   <li>{@code config}   - the config class to find the field in</li>
 *   <li>{@code name}     - the name of the field to get the value of</li>
 *   <li>{@code inverted} - whether the condition should be inverted, so it will pass if the config field is false instead.
 *                          Inclusion is optional.</li>
 * </ul></p>
 *
 * @see DataUtil#registerBooleanConfigCondition(String, boolean, Object...)
 * @author abigailfails
 *
 */
public class BooleanConfigCondition implements ICondition {
    private final Field configField;
    private final Object configObject;
    private final String configObjectName;
    private final String configFieldName;
    private final boolean isInverted;
    private final ResourceLocation location;

    public BooleanConfigCondition(ResourceLocation location, Object configObject, String configObjectName, Field configField, String configFieldName, boolean isInverted) {
        this.location = location;
        this.configObject = configObject;
        this.configObjectName = configObjectName;
        this.configField = configField;
        this.configFieldName = configFieldName;
        this.isInverted = isInverted;
    }


    @Override
    public ResourceLocation getID() {
        return this.location;
    }

    @Override
    public boolean test() {
        try {
            return this.isInverted != ((ForgeConfigSpec.BooleanValue) configField.get(configObject)).get();
        } catch (IllegalAccessException e) {
            AbnormalsCore.LOGGER.error("Cannot access field \"" + configField.getName() + "\" for config condition \"" + this.getID() + "\"");
            return false;
        }
    }

    public static class Serializer implements IConditionSerializer<BooleanConfigCondition> {
        private static final Hashtable<String, Field> CONFIG_FIELDS = new Hashtable<>();
        private static final Hashtable<String, Object> CONFIG_OBJECTS = new Hashtable<>();
        private final ResourceLocation location;

        public Serializer(String modId, Map<String, Field> newConfigFields, Map<String, Object> newConfigObjects) {
            this.location = new ResourceLocation(modId, "config");
            CONFIG_FIELDS.putAll(newConfigFields);
            CONFIG_OBJECTS.putAll(newConfigObjects);
        }

        @Override
        public void write(JsonObject json, BooleanConfigCondition value) {
            json.addProperty("config", value.configObjectName);
            json.addProperty("name", value.configFieldName);
            if (value.isInverted) json.addProperty("inverted", true);
        }

        @Override
        public BooleanConfigCondition read(JsonObject json) {
            if (!json.has("config"))
                throw new JsonSyntaxException("Missing \"config\", expected to find a string");
            if (!json.has("name"))
                throw new JsonSyntaxException("Missing \"name\", expected to find a string");
            String configObjectName = JSONUtils.getString(json, "config");
            Object configObject = CONFIG_OBJECTS.get(configObjectName);
            String configFieldName = JSONUtils.getString(json, "name");
            Field configField = CONFIG_FIELDS.get(configFieldName);
            if (configObject == null)
                throw new JsonSyntaxException("No config class of name \"" + configObjectName + "\" found");
            if (configField == null)
                throw new JsonSyntaxException("No config value of name \"" + configFieldName + "\" found");
            try {
                //checks whether the config object actually has the specified field, and whether it can be accessed
                //noinspection ResultOfMethodCallIgnored
                configField.get(configObject);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new JsonSyntaxException("Config value \"" + configFieldName + "\" " + (e instanceof IllegalArgumentException ? "not found" : "inaccessible") +  " in class \"" + configObjectName + "\"");
            }
            return new BooleanConfigCondition(location, configObject, configObjectName, configField, configFieldName, json.has("inverted") && JSONUtils.getBoolean(json, "inverted"));
        }

        @Override
        public ResourceLocation getID() {
            return this.location;
        }
    }
}
