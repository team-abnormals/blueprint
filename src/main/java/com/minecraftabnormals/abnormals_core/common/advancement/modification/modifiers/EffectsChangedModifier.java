package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.google.gson.JsonObject;
import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.EffectsChangedTrigger;
import net.minecraft.advancements.criterion.MobEffectsPredicate;
import net.minecraft.potion.Effect;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * An {@link AdvancementModifier} extension that modifies all_effects type criteria.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class EffectsChangedModifier extends AdvancementModifier<EffectsChangedModifier.Config> {
	private static final Field INSTANCE_EFFECTS_FIELD = ObfuscationReflectionHelper.findField(EffectsChangedTrigger.Instance.class, "field_193196_a");
	private static final Field PREDICATE_EFFECTS_FIELD = ObfuscationReflectionHelper.findField(MobEffectsPredicate.class, "field_193474_b");

	public EffectsChangedModifier() {
		super((element, conditionArrayParser) -> {
			JsonObject object = element.getAsJsonObject();
			String criteria = JSONUtils.getString(object, "criteria");
			boolean removes = JSONUtils.getBoolean(object, "removes");
			MobEffectsPredicate effectsPredicate = MobEffectsPredicate.deserialize(object.get("effects"));
			return new Config(criteria, removes, effectsPredicate);
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modify(Advancement.Builder builder, Config config) {
		String criteriaKey = config.criteria;
		Criterion criterion = builder.getCriteria().get(criteriaKey);
		if (criterion != null) {
			ICriterionInstance instance = criterion.getCriterionInstance();
			if (instance instanceof EffectsChangedTrigger.Instance) {
				try {
					Map<Effect, MobEffectsPredicate.InstancePredicate> effectMap = (Map<Effect, MobEffectsPredicate.InstancePredicate>) PREDICATE_EFFECTS_FIELD.get(INSTANCE_EFFECTS_FIELD.get(instance));
					Map<Effect, MobEffectsPredicate.InstancePredicate> configEffectMap = (Map<Effect, MobEffectsPredicate.InstancePredicate>) PREDICATE_EFFECTS_FIELD.get(config.mobEffectsPredicate);
					if (config.removes) {
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

	static class Config {
		private final String criteria;
		private final boolean removes;
		private final MobEffectsPredicate mobEffectsPredicate;

		Config(String criteria, boolean removes, MobEffectsPredicate mobEffectsPredicate) {
			this.criteria = criteria;
			this.removes = removes;
			this.mobEffectsPredicate = mobEffectsPredicate;
		}
	}
}
