package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * An {@link AdvancementModifier} implementation that modifies 'effects_changed' type criteria.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record EffectsChangedModifier(String criteria, boolean removes, MobEffectsPredicate mobEffectsPredicate) implements AdvancementModifier<EffectsChangedModifier> {
	private static final Field INSTANCE_EFFECTS_FIELD = ObfuscationReflectionHelper.findField(EffectsChangedTrigger.TriggerInstance.class, "f_26774_");
	private static final Field PREDICATE_EFFECTS_FIELD = ObfuscationReflectionHelper.findField(MobEffectsPredicate.class, "f_56548_");

	@SuppressWarnings("unchecked")
	@Override
	public void modify(Advancement.Builder builder) {
		String criteriaKey = this.criteria;
		Criterion criterion = builder.getCriteria().get(criteriaKey);
		if (criterion != null) {
			CriterionTriggerInstance instance = criterion.getTrigger();
			if (instance instanceof EffectsChangedTrigger.TriggerInstance) {
				try {
					Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> effectMap = (Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate>) PREDICATE_EFFECTS_FIELD.get(INSTANCE_EFFECTS_FIELD.get(instance));
					Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> configEffectMap = (Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate>) PREDICATE_EFFECTS_FIELD.get(this.mobEffectsPredicate);
					if (this.removes) {
						configEffectMap.forEach((effect, instancePredicate) -> effectMap.remove(effect));
					} else {
						effectMap.putAll(configEffectMap);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
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
		@Override
		public JsonElement serialize(EffectsChangedModifier modifier, Void additional) throws JsonParseException {
			JsonObject object = new JsonObject();
			object.addProperty("criteria", modifier.criteria);
			object.addProperty("removes", modifier.removes);
			object.add("effects", modifier.mobEffectsPredicate.serializeToJson());
			return object;
		}

		@Override
		public EffectsChangedModifier deserialize(JsonElement element, DeserializationContext additional) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			String criteria = GsonHelper.getAsString(object, "criteria");
			boolean removes = GsonHelper.getAsBoolean(object, "removes");
			MobEffectsPredicate effectsPredicate = MobEffectsPredicate.fromJson(object.get("effects"));
			return new EffectsChangedModifier(criteria, removes, effectsPredicate);
		}
	}
}
