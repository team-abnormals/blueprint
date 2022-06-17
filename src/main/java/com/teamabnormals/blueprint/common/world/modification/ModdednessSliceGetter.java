package com.teamabnormals.blueprint.common.world.modification;

import net.minecraft.resources.ResourceLocation;

/**
 * The interface used for adding certain methods used by {@link com.teamabnormals.blueprint.core.registry.BlueprintSurfaceRules.ModdednessSliceConditionSource} instances.
 *
 * @author SmellyModder (Luke Tonon)
 * @see com.teamabnormals.blueprint.core.mixin.SurfaceRulesContextMixin
 */
public interface ModdednessSliceGetter {
	/**
	 * Checks if this getter cannot process {@link #getSliceName()} successfully.
	 *
	 * @return If this getter cannot process {@link #getSliceName()} successfully.
	 */
	boolean cannotGetSlices();

	/**
	 * Gets the name of a modded provider at the current position.
	 *
	 * @return The name of a modded provider at the current position.
	 */
	ResourceLocation getSliceName();
}
