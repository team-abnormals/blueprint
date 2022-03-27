package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An {@link AdvancementModifier} implementation that modifies the criteria and requirements of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record CriteriaModifier(Map<String, Criterion> criteria, Optional<String[][]> requirements, boolean shouldReplaceRequirements, Optional<List<IndexedRequirementsEntry>> indexedRequirements) implements AdvancementModifier<CriteriaModifier> {
	public static final Field REQUIREMENTS_FIELD = ObfuscationReflectionHelper.findField(Advancement.Builder.class, "f_138337_");

	/**
	 * Creates a new {@link Builder} instance to simplify creation of {@link CriteriaModifier} instances.
	 *
	 * @param modId The ID of the mod that's creating this builder.
	 * @return A new {@link Builder} instance.
	 */
	public static Builder builder(String modId) {
		return new Builder(modId);
	}

	@Override
	public void modify(Advancement.Builder builder) {
		builder.getCriteria().putAll(this.criteria);
		try {
			var requirements = this.requirements;
			if (requirements.isPresent()) {
				var theseRequirements = requirements.get();
				if (this.shouldReplaceRequirements) {
					REQUIREMENTS_FIELD.set(builder, theseRequirements);
				} else
					REQUIREMENTS_FIELD.set(builder, ArrayUtils.addAll((String[][]) REQUIREMENTS_FIELD.get(builder), theseRequirements));
			}
			var indexedRequirements = this.indexedRequirements;
			if (indexedRequirements.isPresent()) {
				String[][] builderRequirements = (String[][]) REQUIREMENTS_FIELD.get(builder);
				int length = builderRequirements.length;
				for (IndexedRequirementsEntry entry : indexedRequirements.get()) {
					int index = entry.index;
					if (index < length) {
						builderRequirements[index] = entry.replace ? entry.requirements : ArrayUtils.addAll(builderRequirements[index], entry.requirements);
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.CRITERIA;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<CriteriaModifier> {
		@Override
		public JsonElement serialize(CriteriaModifier modifier, Void additional) throws JsonParseException {
			JsonObject jsonObject = new JsonObject();
			JsonObject criteriaObject = new JsonObject();
			var criteria = modifier.criteria;
			criteria.forEach((key, criterion) -> criteriaObject.add(key, criterion.serializeToJson()));
			jsonObject.add("criteria", criteriaObject);
			modifier.requirements.ifPresent(requirements -> {
				JsonArray requirementsArray = new JsonArray();
				for (String[] astring : requirements) {
					JsonArray jsonarray = new JsonArray();
					for (String s : astring) {
						if (criteria.containsKey(s)) throw new JsonParseException("Unknown criterion: " + s);
						jsonarray.add(s);
					}
					requirementsArray.add(jsonarray);
				}
				jsonObject.add("requirements", requirementsArray);
				jsonObject.addProperty("should_replace_requirements", modifier.shouldReplaceRequirements);
			});
			modifier.indexedRequirements.ifPresent(list -> {
				if (!list.isEmpty()) {
					JsonArray indexedRequirements = new JsonArray();
					list.forEach(indexedRequirementsEntry -> {
						JsonObject entry = new JsonObject();
						entry.addProperty("index", indexedRequirementsEntry.index);
						entry.addProperty("replace", indexedRequirementsEntry.replace);
						JsonArray requirementsArray = new JsonArray();
						for (String key : indexedRequirementsEntry.requirements) {
							if (criteria.containsKey(key)) throw new JsonParseException("Unknown criterion: " + key);
							requirementsArray.add(key);
						}
						entry.add("requirements", requirementsArray);
						indexedRequirements.add(entry);
					});
					jsonObject.add("indexed_requirements", indexedRequirements);
				}
			});
			return jsonObject;
		}

		@Override
		public CriteriaModifier deserialize(JsonElement element, DeserializationContext additional) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			Map<String, Criterion> criteria = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(object, "criteria"), additional);
			if (criteria.isEmpty()) throw new JsonParseException("Criteria cannot be empty!");
			Optional<String[][]> requirements = Optional.empty();
			boolean shouldReplaceRequirements = false;
			if (GsonHelper.isValidNode(object, "requirements")) {
				JsonArray jsonArray = GsonHelper.getAsJsonArray(object, "requirements", new JsonArray());
				String[][] requirementsArray = new String[jsonArray.size()][];

				for (int i = 0; i < jsonArray.size(); ++i) {
					JsonArray requirementsArray2 = GsonHelper.convertToJsonArray(jsonArray.get(i), "requirements[" + i + "]");
					requirementsArray[i] = new String[requirementsArray2.size()];

					for (int j = 0; j < requirementsArray2.size(); ++j) {
						requirementsArray[i][j] = GsonHelper.convertToString(requirementsArray2.get(j), "requirements[" + i + "][" + j + "]");
					}
				}

				if (requirementsArray.length == 0) {
					requirementsArray = new String[criteria.size()][];

					int i = 0;
					for (String key : criteria.keySet()) {
						requirementsArray[i++] = new String[]{key};
					}
				}

				for (String[] requirementArray : requirementsArray) {
					if (requirementArray.length == 0 && criteria.isEmpty()) {
						throw new JsonParseException("Requirement entry cannot be empty");
					}

					for (String criterion : requirementArray) {
						if (!criteria.containsKey(criterion)) {
							throw new JsonParseException("Unknown required criterion '" + criterion + "'");
						}
					}
				}

				requirements = Optional.of(requirementsArray);
				shouldReplaceRequirements = GsonHelper.getAsBoolean(object, "should_replace_requirements");
			}
			Optional<List<IndexedRequirementsEntry>> indexedRequirements = Optional.empty();
			if (GsonHelper.isValidNode(object, "indexed_requirements")) {
				JsonArray indexedRequirementsArray = GsonHelper.getAsJsonArray(object, "indexed_requirements");
				List<IndexedRequirementsEntry> parsedEntries = new LinkedList<>();
				indexedRequirementsArray.forEach(entryElement -> {
					JsonObject entry = entryElement.getAsJsonObject();
					int index = GsonHelper.getAsInt(entry, "index");
					JsonArray requirementsArray = GsonHelper.getAsJsonArray(entry, "requirements");
					int size = requirementsArray.size();
					if (size == 0) throw new JsonParseException("Requirements cannot be empty!");
					String[] keys = new String[size];
					for (int i = 0; i < size; i++) {
						String string = requirementsArray.get(i).getAsString();
						if (!criteria.containsKey(string))
							throw new JsonParseException("Unknown required criterion '" + string + "'");
						keys[i] = string;
					}
					parsedEntries.add(new IndexedRequirementsEntry(index, GsonHelper.getAsBoolean(entry, "replace"), keys));
				});
				indexedRequirements = Optional.of(parsedEntries);
			}
			return new CriteriaModifier(criteria, requirements, shouldReplaceRequirements, indexedRequirements);
		}
	}

	/**
	 * The record class for storing the configuration of indexed requirements.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static record IndexedRequirementsEntry(int index, boolean replace, String[] requirements) {}

	/**
	 * The builder class for {@link CriteriaModifier} instances.
	 * <p>Use {@link CriteriaModifier#builder(String)} to create new instances of this class.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Builder {
		private final String modId;
		private final Map<String, Criterion> criteria = Maps.newLinkedHashMap();
		private final List<IndexedRequirementsEntry> indexedRequirements = new LinkedList<>();
		@Nullable
		private String[][] requirements;
		private boolean shouldReplaceRequirements = false;

		private Builder(String modId) {
			this.modId = modId;
		}

		/**
		 * Adds a named {@link Criterion} instance to the builder.
		 *
		 * @param key       The name of the {@link Criterion} instance.
		 * @param criterion A {@link Criterion} instance to add.
		 * @return This builder.
		 */
		public Builder addCriterionRaw(String key, Criterion criterion) {
			if (this.criteria.containsKey(key)) throw new IllegalArgumentException("Duplicate criterion: " + key);
			this.criteria.put(key, criterion);
			return this;
		}

		/**
		 * Adds a modId-prefixed {@link Criterion} instance to the builder.
		 *
		 * @param key       The name of the {@link Criterion} instance.
		 * @param criterion A {@link Criterion} instance to add.
		 * @return This builder.
		 */
		public Builder addCriterion(String key, Criterion criterion) {
			return this.addCriterionRaw(this.modId + ":" + key, criterion);
		}

		/**
		 * Adds a modId-prefixed {@link CriterionTriggerInstance} instance to the builder.
		 *
		 * @param key     The name of the {@link CriterionTriggerInstance} instance.
		 * @param trigger A {@link CriterionTriggerInstance} instance to add.
		 * @return This builder.
		 */
		public Builder addCriterion(String key, CriterionTriggerInstance trigger) {
			this.addCriterion(key, new Criterion(trigger));
			return this;
		}

		/**
		 * Sets the {@link #requirements}.
		 *
		 * @param requirements A requirements array.
		 * @return This builder.
		 */
		public Builder requirements(String[][] requirements) {
			this.requirements = requirements;
			return this;
		}

		/**
		 * Sets the {@link #requirements} to be a collection of strings arranged by a {@link RequirementsStrategy} instance.
		 *
		 * @param requirements         A collection of requirements.
		 * @param requirementsStrategy A {@link RequirementsStrategy} instance to use for arranging the requirements.
		 * @return This builder.
		 */
		public Builder requirements(Collection<String> requirements, RequirementsStrategy requirementsStrategy) {
			this.requirements = requirementsStrategy.createRequirements(requirements);
			return this;
		}

		/**
		 * Sets the {@link #requirements} to the {@link #criteria} keys arranged by a {@link RequirementsStrategy} instance.
		 *
		 * @param requirementsStrategy A {@link RequirementsStrategy} instance to use for arranging the requirements.
		 * @return This builder.
		 */
		public Builder requirements(RequirementsStrategy requirementsStrategy) {
			this.requirements = requirementsStrategy.createRequirements(this.criteria.keySet());
			return this;
		}

		/**
		 * Sets if the modifier should replace an advancement's requirements with the {@link #requirements}.
		 *
		 * @param shouldReplaceRequirements If the modifier should replace an advancement's requirements.
		 * @return This builder.
		 */
		public Builder shouldReplaceRequirements(boolean shouldReplaceRequirements) {
			this.shouldReplaceRequirements = shouldReplaceRequirements;
			return this;
		}

		/**
		 * Creates and adds a {@link IndexedRequirementsEntry} instance to this builder.
		 *
		 * @param index        The index of the requirements to modify.
		 * @param replace      If the requirements at the index should get replaced.
		 * @param requirements An array of requirements to use.
		 * @return This builder.
		 */
		public Builder addIndexedRequirementsRaw(int index, boolean replace, String... requirements) {
			this.indexedRequirements.add(new IndexedRequirementsEntry(index, replace, requirements));
			return this;
		}

		/**
		 * Creates and adds a modId-prefixed {@link IndexedRequirementsEntry} instance to this builder.
		 *
		 * @param index        The index of the requirements to modify.
		 * @param replace      If the requirements at the index should get replaced.
		 * @param requirements An array of requirements to use.
		 * @return This builder.
		 */
		public Builder addIndexedRequirements(int index, boolean replace, String... requirements) {
			int length = requirements.length;
			String[] prefixedRequirements = new String[length];
			String prefix = this.modId + ":";
			for (int i = 0; i < length; i++) prefixedRequirements[i] = prefix + requirements[i];
			return this.addIndexedRequirementsRaw(index, replace, prefixedRequirements);
		}

		/**
		 * Builds a new {@link CriteriaModifier} instance.
		 *
		 * @return A new {@link CriteriaModifier} instance.
		 */
		public CriteriaModifier builder() {
			var criteria = this.criteria;
			if (criteria.isEmpty()) throw new IllegalStateException("Cannot have no criteria!");
			return new CriteriaModifier(ImmutableMap.copyOf(criteria), Optional.ofNullable(this.requirements), this.shouldReplaceRequirements, Optional.of(ImmutableList.copyOf(this.indexedRequirements)));
		}
	}
}
