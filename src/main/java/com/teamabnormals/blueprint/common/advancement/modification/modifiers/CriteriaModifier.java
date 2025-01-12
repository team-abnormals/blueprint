package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.RegistryOps;

import javax.annotation.Nullable;
import java.util.*;

/**
 * An {@link AdvancementModifier} implementation that modifies the criteria and requirements of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record CriteriaModifier(Map<String, Criterion<?>> criteria, Optional<AdvancementRequirements> requirements,
							   boolean shouldReplaceRequirements,
							   Optional<List<IndexedRequirementsEntry>> indexedRequirements) implements AdvancementModifier<CriteriaModifier> {
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
		builder.criteria.putAll(this.criteria);
		var requirementsToAdd = this.requirements;
		if (requirementsToAdd.isPresent()) {
			if (this.shouldReplaceRequirements || builder.requirements.isEmpty()) {
				builder.requirements = requirementsToAdd;
			} else {
				var newRequirements = new ArrayList<>(builder.requirements.get().requirements());
				newRequirements.addAll(requirementsToAdd.get().requirements());
				builder.requirements = Optional.of(new AdvancementRequirements(newRequirements));
			}
		}
		var requirements = builder.requirements;
		if (requirements.isEmpty()) return;
		var indexedRequirements = this.indexedRequirements;
		if (indexedRequirements.isPresent()) {
			var newRequirements = new ArrayList<>(requirements.get().requirements());
			int length = newRequirements.size();
			for (IndexedRequirementsEntry entry : indexedRequirements.get()) {
				int index = entry.index;
				if (index < length) {
					if (entry.replace) {
						newRequirements.set(index, entry.requirements);
					} else {
						var newList = new ArrayList<>(newRequirements.get(index));
						newList.addAll(entry.requirements);
						newRequirements.set(index, newList);
					}
				}
			}
			builder.requirements = Optional.of(new AdvancementRequirements(newRequirements));
		}
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.CRITERIA;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<CriteriaModifier> {
		private static final Codec<Map<String, Criterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, Criterion.CODEC)
				.validate(map -> map.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success(map));
		private static final Codec<CriteriaModifier> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						CRITERIA_CODEC.fieldOf("criteria").forGetter(CriteriaModifier::criteria),
						AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter(CriteriaModifier::requirements),
						Codec.BOOL.fieldOf("should_replace_requirements").forGetter(CriteriaModifier::shouldReplaceRequirements),
						IndexedRequirementsEntry.CODEC.listOf().optionalFieldOf("indexed_requirements").forGetter(CriteriaModifier::indexedRequirements)
				).apply(instance, CriteriaModifier::new)
		);

		@Override
		public JsonElement serialize(CriteriaModifier modifier, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.encodeStart(ops, modifier);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get();
		}

		@Override
		public CriteriaModifier deserialize(JsonElement element, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.decode(ops, element);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get().getFirst();
		}
	}

	/**
	 * The record class for storing the configuration of indexed requirements.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record IndexedRequirementsEntry(int index, boolean replace, List<String> requirements) {
		private static final Codec<IndexedRequirementsEntry> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.INT.fieldOf("index").forGetter(IndexedRequirementsEntry::index),
						Codec.BOOL.fieldOf("replace").forGetter(IndexedRequirementsEntry::replace),
						Codec.STRING.listOf().fieldOf("requirements").forGetter(IndexedRequirementsEntry::requirements)
				).apply(instance, IndexedRequirementsEntry::new)
		);
	}

	/**
	 * The builder class for {@link CriteriaModifier} instances.
	 * <p>Use {@link CriteriaModifier#builder(String)} to create new instances of this class.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Builder {
		private final String modId;
		private final Map<String, Criterion<?>> criteria = Maps.newLinkedHashMap();
		private final List<IndexedRequirementsEntry> indexedRequirements = new LinkedList<>();
		@Nullable
		private AdvancementRequirements requirements;
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
		public Builder addCriterionRaw(String key, Criterion<?> criterion) {
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
		public Builder addCriterion(String key, Criterion<?> criterion) {
			return this.addCriterionRaw(this.modId + ":" + key, criterion);
		}

		/**
		 * Sets the {@link #requirements}.
		 *
		 * @param requirements A requirements array.
		 * @return This builder.
		 */
		public Builder requirements(List<List<String>> requirements) {
			this.requirements = new AdvancementRequirements(requirements);
			return this;
		}

		/**
		 * Sets the {@link #requirements} to be a collection of strings arranged by a {@link AdvancementRequirements.Strategy} instance.
		 *
		 * @param requirements         A collection of requirements.
		 * @param requirementsStrategy A {@link AdvancementRequirements.Strategy} instance to use for arranging the requirements.
		 * @return This builder.
		 */
		public Builder requirements(Collection<String> requirements, AdvancementRequirements.Strategy requirementsStrategy) {
			this.requirements = requirementsStrategy.create(requirements);
			return this;
		}

		/**
		 * Sets the {@link #requirements} to the {@link #criteria} keys arranged by a {@link AdvancementRequirements.Strategy} instance.
		 *
		 * @param requirementsStrategy A {@link AdvancementRequirements.Strategy} instance to use for arranging the requirements.
		 * @return This builder.
		 */
		public Builder requirements(AdvancementRequirements.Strategy requirementsStrategy) {
			this.requirements = requirementsStrategy.create(this.criteria.keySet());
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
			this.indexedRequirements.add(new IndexedRequirementsEntry(index, replace, List.of(requirements)));
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
		public CriteriaModifier build() {
			var criteria = this.criteria;
			if (criteria.isEmpty()) throw new IllegalStateException("Cannot have no criteria!");
			return new CriteriaModifier(ImmutableMap.copyOf(criteria), Optional.ofNullable(this.requirements), this.shouldReplaceRequirements, Optional.of(ImmutableList.copyOf(this.indexedRequirements)));
		}
	}
}
