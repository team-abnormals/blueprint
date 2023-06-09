package com.teamabnormals.blueprint.client.renderer;

import com.teamabnormals.blueprint.core.registry.BlueprintBoatTypes;

/**
 * The interface for getting the type belonging to a Blueprint Boat.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface HasBlueprintBoatType {
	/**
	 * Gets this Blueprint Boat's {@link BlueprintBoatTypes.BlueprintBoatType} instance.
	 *
	 * @return This Blueprint Boat's {@link BlueprintBoatTypes.BlueprintBoatType} instance.
	 */
	BlueprintBoatTypes.BlueprintBoatType getBoatType();
}
