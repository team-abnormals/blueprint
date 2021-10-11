package com.minecraftabnormals.abnormals_core.common.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * A {@link MobEffect} extension used to get access to the protected constructor in the {@link MobEffect} class.
 */
public class AbnormalsMobEffect extends MobEffect {

	public AbnormalsMobEffect(MobEffectCategory effectType, int liquidColor) {
		super(effectType, liquidColor);
	}

}