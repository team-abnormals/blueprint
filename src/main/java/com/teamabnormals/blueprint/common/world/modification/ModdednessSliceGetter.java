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
	 * Checks if this getter cannot process {@link #getSliceName(int, int)} successfully.
	 *
	 * @return If this getter cannot process {@link #getSliceName(int, int)} successfully.
	 */
	boolean cannotGetSlices();

	/**
	 * Gets the name of a modded provider at a horizontal position.
	 *
	 * @param x The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
	 * @param z The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
	 * @return The name of a modded provider at a horizontal position.
	 */
	ResourceLocation getSliceName(int x, int z);
}
