package com.minecraftabnormals.abnormals_core.core.api;

import net.minecraft.world.entity.LivingEntity;

/**
 * Used to make a living entity that doesn't extend AgeableEntity compatible with Quark's potato poisoning and Savage & Ravage's Growth & Youth Potions.
 * <p>Classes implementing this must extend {@link LivingEntity}.</p>
 *
 * @author abigailfails
 */
public interface IAgeableEntity {
	/**
	 * Checks If the entity will make any natural progress towards the next stage. If false, potato poisoning will not work.
	 *
	 * @return If the entity will make any natural progress towards the next stage.
	 */
	boolean hasGrowthProgress();

	/**
	 * If the entity can grow to a higher stage, resets any progress made towards it (e.g. age timer).
	 */
	void resetGrowthProgress();

	/**
	 * @param growingUp True if this should check for a higher growth stage or false for a lower one.
	 * @return If the entity can grow or regress into another stage depending on growingUp.
	 */
	boolean canAge(boolean growingUp);

	/**
	 * Attempts to change the entity's growth stage depending on growingUp.
	 *
	 * @param growingUp True if this should try to grow the entity, or false to try to regress the entity.
	 * @return The entity this ages into - if growing is implemented such that this doesn't change, it returns itself.
	 */
	LivingEntity attemptAging(boolean growingUp);
}
