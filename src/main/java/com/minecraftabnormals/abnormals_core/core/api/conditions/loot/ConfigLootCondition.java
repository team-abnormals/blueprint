package com.minecraftabnormals.abnormals_core.core.api.conditions.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config.IConfigPredicate;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config.IConfigPredicateSerializer;
import com.minecraftabnormals.abnormals_core.core.api.conditions.ConfigCondition;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * A loot condition that is registered and functions exactly the same way as {@link ConfigCondition}, but for loot tables instead.
 *
 * @author abigailfails
 */
public class ConfigLootCondition implements ILootCondition {
    private final ForgeConfigSpec.ConfigValue<?> value;
    private final String valueID;
    private final Map<IConfigPredicate, Boolean> predicates;
    private final boolean inverted;
    private final ResourceLocation location;

    public ConfigLootCondition(ResourceLocation location, ForgeConfigSpec.ConfigValue<?> value, String valueID, Map<IConfigPredicate, Boolean> predicates, boolean inverted) {
        this.location = location;
        this.value = value;
        this.valueID = valueID;
        this.predicates = predicates;
        this.inverted = inverted;
    }

    @Override
    public LootConditionType func_230419_b_() {
        return Registry.LOOT_CONDITION_TYPE.getOrDefault(this.location);
    }

    @Override
    public boolean test(LootContext context) {
        boolean returnValue;
        if (predicates.size() > 0) {
            returnValue = this.predicates.keySet().stream().allMatch(c -> this.predicates.get(c) != c.test(value));
        } else if (value.get() instanceof Boolean) {
            returnValue = (Boolean) value.get();
        } else throw new IllegalStateException("Predicates required for non-boolean ConfigLootCondition, but none found");
        return this.inverted != returnValue;
    }

    public static class Serializer implements ILootSerializer<ConfigLootCondition> {
        private final Map<String, ForgeConfigSpec.ConfigValue<?>> configValues;
        private final ResourceLocation location;

        public Serializer(String modId, Map<String, ForgeConfigSpec.ConfigValue<?>> configValues) {
            this.location = new ResourceLocation(modId, "config");
            this.configValues = configValues;
        }

        @Override
        public void serialize(JsonObject json, ConfigLootCondition value, JsonSerializationContext context) {
            json.addProperty("value", value.valueID);
            if (!value.predicates.isEmpty()) {
                JsonArray predicates = new JsonArray();
                for (Map.Entry<IConfigPredicate, Boolean> predicatePair : value.predicates.entrySet()) {
                    IConfigPredicate predicate = predicatePair.getKey();
                    ResourceLocation predicateID = predicate.getID();
                    JsonObject object = new JsonObject();
                    object.addProperty("type", predicateID.toString());
                    ConfigCondition.Serializer.CONFIG_PREDICATE_SERIALIZERS.get(predicateID).write(object, predicate);
                    object.addProperty("inverted", predicatePair.getValue());
                    predicates.add(object);
                }
                json.add("predicates", predicates);
            }
            if (value.inverted) json.addProperty("inverted", true);
        }

        @Override
        public ConfigLootCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            if (!json.has("value"))
                throw new JsonSyntaxException("Missing 'value', expected to find a string");
            String name = JSONUtils.getString(json, "value");
            ForgeConfigSpec.ConfigValue<?> configValue = this.configValues.get(name);
            if (configValue == null)
                throw new JsonSyntaxException("No config value of name '" + name + "' found");
            Map<IConfigPredicate, Boolean> predicates = new HashMap<>();
            if (JSONUtils.hasField(json, "predicates")) {
                for (JsonElement predicateElement : JSONUtils.getJsonArray(json, "predicates")) {
                    if (!predicateElement.isJsonObject())
                        throw new JsonSyntaxException("Predicates must be an array of JsonObjects");
                    JsonObject predicateObject = predicateElement.getAsJsonObject();
                    ResourceLocation type = new ResourceLocation(JSONUtils.getString(predicateObject, "type"));
                    IConfigPredicateSerializer<?> serializer = ConfigCondition.Serializer.CONFIG_PREDICATE_SERIALIZERS.get(type);
                    if (serializer == null)
                        throw new JsonSyntaxException("Unknown predicate type: " + type.toString());
                    predicates.put(serializer.read(predicateObject), predicateObject.has("inverted") && JSONUtils.getBoolean(predicateObject, "inverted"));
                }
            } else if (!(configValue.get() instanceof Boolean)) {
                throw new JsonSyntaxException("Missing 'predicates' for non-boolean config value '" + name + "', expected to find an array");
            }
            return new ConfigLootCondition(location, configValue, name, predicates, json.has("inverted") && JSONUtils.getBoolean(json, "inverted"));
        }
    }
}
