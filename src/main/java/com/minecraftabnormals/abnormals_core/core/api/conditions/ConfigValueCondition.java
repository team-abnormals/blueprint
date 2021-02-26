package com.minecraftabnormals.abnormals_core.core.api.conditions;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config.IConfigPredicate;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config.IConfigPredicateSerializer;
import com.minecraftabnormals.abnormals_core.core.util.DataUtil;
import com.minecraftabnormals.abnormals_core.core.annotations.ConfigKey;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * A condition that checks against the values of config values annotated with {@link ConfigKey}.
 *
 * <p>To make your mod's config values compatible with it, annotate them with {@link ConfigKey} taking in the string
 * value that should be used to deserialize the field, then call {@link DataUtil#registerConfigCondition(String, Object...)}
 * in the mod setup, passing your mod id as the first parameter, and the config objects with the
 * {@link ForgeConfigSpec.ConfigValue} instances that should be checked against as the second.</p>
 *
 * <p>For a condition with type {@code "[modid]:config"}, it takes the arguments:
 * <ul>
 *   <li>{@code value}      - the name of the config value to check against, defined by its corresponding {@link ConfigKey} annotation</li>
 *   <li>{@code predicates} - an array of JSON objects that deserialize to an {@link IConfigPredicate}, which prevent
 *                            the condition from passing if one of more of yhem return false. Optional if {@code value}
 *                            maps to a boolean {@link ForgeConfigSpec.ConfigValue}.</li>
 *   <li>{@code inverted}   - whether the condition should be inverted, so it will pass if {@code predicates} return false instead.
 *                            Inclusion is optional.</li>
 * </ul></p>
 *
 * @see DataUtil#registerConfigCondition(String, Object...)
 * @author abigailfails
 */
public class ConfigValueCondition implements ICondition {
    private final ForgeConfigSpec.ConfigValue<?> value;
    private final String valueID;
    private final Map<IConfigPredicate, Boolean> predicates;
    private final boolean inverted;
    private final ResourceLocation location;

    public ConfigValueCondition(ResourceLocation location, ForgeConfigSpec.ConfigValue<?> value, String valueID, Map<IConfigPredicate, Boolean> predicates, boolean inverted) {
        this.location = location;
        this.value = value;
        this.valueID = valueID;
        this.predicates = predicates;
        this.inverted = inverted;
    }

    @Override
    public ResourceLocation getID() {
        return this.location;
    }

    @Override
    public boolean test() {
        boolean returnValue;
        if (predicates.size() > 0) {
            returnValue = this.predicates.keySet().stream().allMatch(c -> this.predicates.get(c) != c.test(value));
        } else if (value.get() instanceof Boolean) {
            returnValue = (Boolean) value.get();
        } else throw new IllegalStateException("Predicates required for non-boolean ConfigValue, but none found");
        return this.inverted != returnValue;
    }

    public static class Serializer implements IConditionSerializer<ConfigValueCondition> {
        public static final Hashtable<ResourceLocation, IConfigPredicateSerializer<?>> CONFIG_PREDICATE_SERIALIZERS = new Hashtable<>();
        private final Map<String, ForgeConfigSpec.ConfigValue<?>> configValues;
        private final ResourceLocation location;

        public Serializer(String modId, Map<String, ForgeConfigSpec.ConfigValue<?>> configValues) {
            this.location = new ResourceLocation(modId, "config");
            this.configValues = configValues;
        }

        @Override
        public void write(JsonObject json, ConfigValueCondition value) {
            json.addProperty("value", value.valueID);
            if (!value.predicates.isEmpty()) {
                JsonArray predicates = new JsonArray();
                json.add("predicates", predicates);
                for (Map.Entry<IConfigPredicate, Boolean> predicatePair : value.predicates.entrySet()) {
                    IConfigPredicate predicate = predicatePair.getKey();
                    ResourceLocation predicateID = predicate.getID();
                    JsonObject object = new JsonObject();
                    predicates.add(object);
                    object.addProperty("type", predicateID.toString());
                    CONFIG_PREDICATE_SERIALIZERS.get(predicateID).write(object, predicate);
                    object.addProperty("inverted", predicatePair.getValue());
                }
            }
            if (value.inverted) json.addProperty("inverted", true);
        }

        @Override
        public ConfigValueCondition read(JsonObject json) {
            if (!json.has("value"))
                throw new JsonSyntaxException("Missing 'value', expected to find a string");
            String name = JSONUtils.getString(json, "value");
            ForgeConfigSpec.ConfigValue<?> configValue = configValues.get(name);
            if (configValue == null)
                throw new JsonSyntaxException("No config value of name '" + name + "' found");
            Map<IConfigPredicate, Boolean> predicates = new HashMap<>();
            if (JSONUtils.hasField(json, "predicates")) {
                for (JsonElement predicateElement : JSONUtils.getJsonArray(json, "predicates")) {
                    if (!predicateElement.isJsonObject())
                        throw new JsonSyntaxException("Predicates must be an array of JsonObjects");
                    JsonObject predicateObject = predicateElement.getAsJsonObject();
                    ResourceLocation type = new ResourceLocation(JSONUtils.getString(predicateObject, "type"));
                    IConfigPredicateSerializer<?> serializer = CONFIG_PREDICATE_SERIALIZERS.get(type);
                    if (serializer == null)
                        throw new JsonSyntaxException("Unknown predicate type: " + type.toString());
                    predicates.put(serializer.read(predicateObject), predicateObject.has("inverted") && JSONUtils.getBoolean(predicateObject, "inverted"));
                }
            } else if (!(configValue.get() instanceof Boolean)) {
                throw new JsonSyntaxException("Missing 'predicates' for non-boolean config value '" + name + "', expected to find an array");
            }
            return new ConfigValueCondition(location, configValue, name, predicates, json.has("inverted") && JSONUtils.getBoolean(json, "inverted"));
        }

        @Override
        public ResourceLocation getID() {
            return this.location;
        }
    }
}
