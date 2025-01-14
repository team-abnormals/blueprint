package com.teamabnormals.blueprint.core.api.conditions.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.core.api.conditions.ConfigValueCondition;
import com.teamabnormals.blueprint.core.api.conditions.config.IConfigPredicate;
import com.teamabnormals.blueprint.core.api.conditions.config.IConfigPredicateSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link LootItemCondition} implementation that is registered and functions exactly the same way as {@link ConfigValueCondition}, but for loot tables instead.
 *
 * @author abigailfails
 */
public class ConfigLootCondition implements LootItemCondition {
	private final LootItemConditionType type;
	private final ModConfigSpec.ConfigValue<?> value;
	private final String valueID;
	private final Map<IConfigPredicate, Boolean> predicates;
	private final boolean inverted;

	public ConfigLootCondition(LootItemConditionType type, ModConfigSpec.ConfigValue<?> value, String valueID, Map<IConfigPredicate, Boolean> predicates, boolean inverted) {
		this.type = type;
		this.value = value;
		this.valueID = valueID;
		this.predicates = predicates;
		this.inverted = inverted;
	}

	@Override
	public LootItemConditionType getType() {
		return this.type;
	}

	@Override
	public boolean test(LootContext context) {
		boolean returnValue;
		if (predicates.size() > 0) {
			returnValue = this.predicates.keySet().stream().allMatch(c -> this.predicates.get(c) != c.test(value));
		} else if (value.get() instanceof Boolean bool) {
			returnValue = bool;
		} else
			throw new IllegalStateException("Predicates required for non-boolean ConfigLootCondition, but none found");
		return this.inverted != returnValue;
	}

	public static class ConfigSerializer implements Codec<ConfigLootCondition> {
		private final Map<String, ModConfigSpec.ConfigValue<?>> configValues;
		private LootItemConditionType type;

		public ConfigSerializer(Map<String, ModConfigSpec.ConfigValue<?>> configValues) {
			this.configValues = configValues;
		}

		public static LootItemConditionType asType(Map<String, ModConfigSpec.ConfigValue<?>> configValues) {
			var serializer = new ConfigSerializer(configValues);
			LootItemConditionType type = new LootItemConditionType(serializer.fieldOf("value"));
			serializer.type = type;
			return type;
		}

		@Override
		public <T> DataResult<Pair<ConfigLootCondition, T>> decode(DynamicOps<T> ops, T input) {
			return ExtraCodecs.JSON.decode(ops, input).flatMap(pair -> {
				JsonElement jsonElement = pair.getFirst();
				if (!(jsonElement instanceof JsonObject json)) return DataResult.error(() -> "Expected a JSON object");
				if (!json.has("value"))
					throw new JsonSyntaxException("Missing 'value', expected to find a string");
				String name = GsonHelper.getAsString(json, "value");
				ModConfigSpec.ConfigValue<?> configValue = this.configValues.get(name);
				if (configValue == null)
					throw new JsonSyntaxException("No config value of name '" + name + "' found");
				Map<IConfigPredicate, Boolean> predicates = new HashMap<>();
				if (GsonHelper.isValidNode(json, "predicates")) {
					for (JsonElement predicateElement : GsonHelper.getAsJsonArray(json, "predicates")) {
						if (!predicateElement.isJsonObject())
							throw new JsonSyntaxException("Predicates must be an array of JsonObjects");
						JsonObject predicateObject = predicateElement.getAsJsonObject();
						ResourceLocation type = ResourceLocation.parse(GsonHelper.getAsString(predicateObject, "type"));
						IConfigPredicateSerializer<?> serializer = ConfigValueCondition.Serializer.CONFIG_PREDICATE_SERIALIZERS.get(type);
						if (serializer == null)
							throw new JsonSyntaxException("Unknown predicate type: " + type);
						predicates.put(serializer.read(predicateObject), predicateObject.has("inverted") && GsonHelper.getAsBoolean(predicateObject, "inverted"));
					}
				} else if (!(configValue.get() instanceof Boolean)) {
					throw new JsonSyntaxException("Missing 'predicates' for non-boolean config value '" + name + "', expected to find an array");
				}
				return DataResult.success(Pair.of(new ConfigLootCondition(this.type, configValue, name, predicates, json.has("inverted") && GsonHelper.getAsBoolean(json, "inverted")), input));
			});
		}

		@Override
		public <T> DataResult<T> encode(ConfigLootCondition value, DynamicOps<T> ops, T prefix) {
			JsonObject json = new JsonObject();
			json.addProperty("value", value.valueID);
			if (!value.predicates.isEmpty()) {
				JsonArray predicates = new JsonArray();
				for (Map.Entry<IConfigPredicate, Boolean> predicatePair : value.predicates.entrySet()) {
					IConfigPredicate predicate = predicatePair.getKey();
					ResourceLocation predicateID = predicate.getID();
					JsonObject object = new JsonObject();
					object.addProperty("type", predicateID.toString());
					ConfigValueCondition.Serializer.CONFIG_PREDICATE_SERIALIZERS.get(predicateID).write(object, predicate);
					object.addProperty("inverted", predicatePair.getValue());
					predicates.add(object);
				}
				json.add("predicates", predicates);
			}
			if (value.inverted) json.addProperty("inverted", true);
			return ExtraCodecs.JSON.encode(json, ops, prefix);
		}
	}
}
