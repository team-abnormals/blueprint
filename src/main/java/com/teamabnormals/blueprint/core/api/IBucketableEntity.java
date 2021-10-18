package com.teamabnormals.blueprint.core.api;

import net.minecraft.world.item.ItemStack;

/**
 * An interface representing an entity that can be bucketed.
 */
public interface IBucketableEntity {
	/**
	 * Gets the bucket for the entity.
	 *
	 * @return The bucket for the entity.
	 */
	ItemStack getBucket();
}
