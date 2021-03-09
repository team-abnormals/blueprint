package com.minecraftabnormals.abnormals_core.core.api;

/**
 * Implemented on chest blocks that make use of AC's chest system.
 */
public interface IChestBlock {
	/**
	 * Gets the chest type ID of this {@link IChestBlock}.
	 * <p>Used on {@link com.minecraftabnormals.abnormals_core.client.ChestManager#getInfoForChest(String)}</p>
	 *
	 * @return The chest type ID of this {@link IChestBlock}.
	 */
	String getChestType();
}
