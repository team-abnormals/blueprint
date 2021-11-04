package com.teamabnormals.blueprint.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * A {@link MobEffect} extension used to get access to the protected constructor in the {@link MobEffect} class.
 */
public class BlueprintMobEffect extends MobEffect {

	public BlueprintMobEffect(MobEffectCategory effectType, int liquidColor) {
		super(effectType, liquidColor);
	}

}