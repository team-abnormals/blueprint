package com.teamabnormals.blueprint.core.api.conditions;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.teamabnormals.blueprint.core.annotations.ConfigKey;
import com.teamabnormals.blueprint.core.api.conditions.config.IConfigPredicate;
import com.teamabnormals.blueprint.core.api.conditions.config.IConfigPredicateSerializer;
import com.teamabnormals.blueprint.core.util.DataUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A condition that checks against the values of config values annotated with {@link ConfigKey}.
 * <p>Uses the recipe system, but is also compatible with modifiers etc.</p>
 *
 * <p>For a condition with type {@code "[modid]:config"}, it takes the arguments:
 * <ul>
 *   <li>{@code value}      - the name of the config value to check against, defined by its corresponding {@link ConfigKey} annotation.</li>
 *   <li>{@code predicates} - an array of JSON objects that deserialize to an {@link IConfigPredicate}, which prevent
 *                            the condition from passing if one of more of them return false. Optional if {@code value}
 *                            maps to a boolean {@link ModConfigSpec.ConfigValue}.</li>
 *   <li>{@code inverted} (optional)   - whether the condition should be inverted, so it will pass if {@code predicates} return false instead.</li>
 * </ul></p>
 *
 * @author abigailfails
 * @see DataUtil#getConfigValues(Object...)
 */
public class ConfigValueCondition implements ICondition {
	private final MapCodec<ConfigValueCondition> codec;
	private final ModConfigSpec.ConfigValue<?> value;
	private final String valueID;
	private final Map<IConfigPredicate, Boolean> predicates;
	private final boolean inverted;

	public ConfigValueCondition(MapCodec<ConfigValueCondition> codec, ModConfigSpec.ConfigValue<?> value, String valueID, Map<IConfigPredicate, Boolean> predicates, boolean inverted) {
		this.codec = codec;
		this.value = value;
		this.valueID = valueID;
		this.predicates = predicates;
		this.inverted = inverted;
	}

	public ConfigValueCondition(MapCodec<ConfigValueCondition> codec, String valueID, Map<IConfigPredicate, Boolean> predicates, boolean inverted) {
		this(codec, null, valueID, predicates, inverted);
	}

	public ConfigValueCondition(MapCodec<ConfigValueCondition> codec, String valueID, boolean inverted) {
		this(codec, valueID, Maps.newHashMap(), inverted);
	}

	public ConfigValueCondition(MapCodec<ConfigValueCondition> codec, String valueID) {
		this(codec, valueID, false);
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return this.codec;
	}

	@Override
	public boolean test(IContext context) {
		boolean returnValue;
		Map<IConfigPredicate, Boolean> predicates = this.predicates;
		ModConfigSpec.ConfigValue<?> value = this.value;
		if (predicates.size() > 0) {
			returnValue = predicates.keySet().stream().allMatch(c -> predicates.get(c) != c.test(value));
		} else if (value.get() instanceof Boolean bool) {
			returnValue = bool;
		} else
			throw new IllegalStateException("Predicates required for non-boolean ConfigLootCondition, but none found");
		return this.inverted != returnValue;
	}

	public static class Serializer extends MapCodec<ConfigValueCondition> {
		public static final HashMap<ResourceLocation, IConfigPredicateSerializer<?>> CONFIG_PREDICATE_SERIALIZERS = new HashMap<>();
		private final Map<String, ModConfigSpec.ConfigValue<?>> configValues;

		public Serializer(Map<String, ModConfigSpec.ConfigValue<?>> configValues) {
			this.configValues = configValues;
		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.of(ops.createString("value"), ops.createString("predicates"), ops.createString("inverted"));
		}

		@Override
		public <T> DataResult<ConfigValueCondition> decode(DynamicOps<T> ops, MapLike<T> input) {
			JsonElement element = ops.convertTo(JsonOps.INSTANCE, ops.createMap(input.entries()));
			if (!(element instanceof JsonObject json)) return DataResult.error(() -> "Expected an object");
			if (!json.has("value"))
				return DataResult.error(() -> "Missing 'value', expected to find a string");
			String name = GsonHelper.getAsString(json, "value");
			ModConfigSpec.ConfigValue<?> configValue = this.configValues.get(name);
			if (configValue == null)
				return DataResult.error(() -> "No config value of name '" + name + "' found");
			Map<IConfigPredicate, Boolean> predicates = new HashMap<>();
			if (GsonHelper.isValidNode(json, "predicates")) {
				for (JsonElement predicateElement : GsonHelper.getAsJsonArray(json, "predicates")) {
					if (!predicateElement.isJsonObject())
						return DataResult.error(() -> "Predicates must be an array of JsonObjects");
					JsonObject predicateObject = predicateElement.getAsJsonObject();
					ResourceLocation type = ResourceLocation.parse(GsonHelper.getAsString(predicateObject, "type"));
					IConfigPredicateSerializer<?> serializer = CONFIG_PREDICATE_SERIALIZERS.get(type);
					if (serializer == null)
						return DataResult.error(() -> "Unknown predicate type: " + type);
					predicates.put(serializer.read(predicateObject), predicateObject.has("inverted") && GsonHelper.getAsBoolean(predicateObject, "inverted"));
				}
			} else if (!(configValue.get() instanceof Boolean)) {
				return DataResult.error(() -> "Missing 'predicates' for non-boolean config value '" + name + "', expected to find an array");
			}
			return DataResult.success(new ConfigValueCondition(this, configValue, name, predicates, json.has("inverted") && GsonHelper.getAsBoolean(json, "inverted")));
		}

		@Override
		public <T> RecordBuilder<T> encode(ConfigValueCondition input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			prefix.add("value", ops.createString(input.valueID));
			if (!input.predicates.isEmpty()) {
				JsonArray predicates = new JsonArray();
				for (Map.Entry<IConfigPredicate, Boolean> predicatePair : input.predicates.entrySet()) {
					IConfigPredicate predicate = predicatePair.getKey();
					ResourceLocation predicateID = predicate.getID();
					JsonObject object = new JsonObject();
					predicates.add(object);
					object.addProperty("type", predicateID.toString());
					CONFIG_PREDICATE_SERIALIZERS.get(predicateID).write(object, predicate);
					object.addProperty("inverted", predicatePair.getValue());
				}
				prefix.add("predicates", JsonOps.INSTANCE.convertTo(ops, predicates));
			}
			if (input.inverted) prefix.add("inverted", ops.createBoolean(true));
			return prefix;
		}
	}
}
