package com.teamabnormals.blueprint.core.api;

import com.teamabnormals.blueprint.client.BlueprintChestMaterials;

/**
 * Implemented on chest blocks that make use of Blueprint's chest system.
 *
 * @author SmellyModder (Luke Tonon)
 * @author bageldotjpg
 */
public interface IChestBlock {
	/**
	 * Gets the chest materials ID of this {@link IChestBlock}.
	 * <p>Used on {@link BlueprintChestMaterials#getMaterials(String)}}.</p>
	 *
	 * @return The chest materials ID of this {@link IChestBlock}.
	 */
	String getChestMaterialsName();
}
