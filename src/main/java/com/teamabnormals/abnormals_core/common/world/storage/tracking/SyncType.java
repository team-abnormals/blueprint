package com.teamabnormals.abnormals_core.common.world.storage.tracking;

/**
 * A enum representing types of syncing.
 * <p> {@link #NOPE} for no syncing. </p>
 * <p> {@link #TO_CLIENT} for syncing to the client player. </p>
 * <p> {@link #TO_CLIENTS} for syncing to all client players. </p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum SyncType {
	NOPE(),
	TO_CLIENT(),
	TO_CLIENTS()
}
