package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import com.teamabnormals.blueprint.common.advancement.modification.BlueprintAdvancementBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.resources.RegistryOps;

import java.util.HashMap;
import java.util.Optional;

/**
 * An {@link AdvancementModifier} implementation that modifies 'effects_changed' type criteria.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record EffectsChangedModifier(String criteria, boolean removes, MobEffectsPredicate mobEffectsPredicate) implements AdvancementModifier<EffectsChangedModifier> {
	@SuppressWarnings("unchecked")
	@Override
	public void modify(BlueprintAdvancementBuilder builder) {
		String criteriaKey = this.criteria;
		Criterion<?> criterion = builder.criteria.get(criteriaKey);
		if (criterion != null) {
			CriterionTriggerInstance instance = criterion.triggerInstance();
			if (instance instanceof EffectsChangedTrigger.TriggerInstance(
					Optional<ContextAwarePredicate> player,
					Optional<MobEffectsPredicate> effects,
					Optional<ContextAwarePredicate> source
			)) {
				if (effects.isEmpty()) {
					if (this.removes) return;
					effects = Optional.of(this.mobEffectsPredicate);
				} else {
					var effectMap = new HashMap<>(effects.get().effectMap());
					if (this.removes) {
						this.mobEffectsPredicate.effectMap().keySet().forEach(effectMap::remove);
					} else {
						effectMap.putAll(this.mobEffectsPredicate.effectMap());
					}
					effects = Optional.of(new MobEffectsPredicate(effectMap));
				}
				builder.addCriterion(criteriaKey, new Criterion<>((CriterionTrigger<EffectsChangedTrigger.TriggerInstance>) criterion.trigger(), new EffectsChangedTrigger.TriggerInstance(player, effects, source)));
			}
		} else {
			throw new IllegalArgumentException("Unknown criteria: " + criteriaKey);
		}
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.EFFECTS_CHANGED;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<EffectsChangedModifier> {
		private static final Codec<EffectsChangedModifier> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.STRING.fieldOf("criteria").forGetter(EffectsChangedModifier::criteria),
						Codec.BOOL.fieldOf("removes").forGetter(EffectsChangedModifier::removes),
						MobEffectsPredicate.CODEC.fieldOf("effects").forGetter(EffectsChangedModifier::mobEffectsPredicate)
				).apply(instance, EffectsChangedModifier::new)
		);

		@Override
		public JsonElement serialize(EffectsChangedModifier modifier, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.encodeStart(ops, modifier);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get();
		}

		@Override
		public EffectsChangedModifier deserialize(JsonElement element, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.decode(ops, element);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get().getFirst();
		}
	}
}
