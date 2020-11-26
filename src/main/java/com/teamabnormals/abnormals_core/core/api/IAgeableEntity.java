package com.teamabnormals.abnormals_core.core.api;

import net.minecraft.entity.LivingEntity;

/***
 * @author abigailfails
 * Use to make a living entity that doesn't extend AgeableEntity compatible with Quark's potato poisoning and
 * Savage & Ravage's Growth & Youth Potions.
 * Implemented class must extend LivingEntity
 */
public interface IAgeableEntity{

    /**
     * If the entity can grow to a higher stage, resets any progress made towards it (e.g. age timer).
     * */
    void resetGrowthProgress();

    /**
    * @param isGrowingUp true checks for a higher growth stage, false checks for a lower one.
    * @return whether the entity has another stage it can grow or regress to depending on isGrowingUp.
    */
    boolean canAge(boolean isGrowingUp);

    /**
     * Attempts to change the entity's growth stage depending on isGrowingUp.
     * @param isGrowingUp true attempts to grow to a higher stage, false attempts to grow to a lower one.
     * @return the entity it ages into - if growing is implemented such that this doesn't change, it returns itself
     * */
    LivingEntity attemptAging(boolean isGrowingUp);
}
