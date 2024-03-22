package com.teamabnormals.blueprint.core.api;

import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.HashMap;

/**
 * Manager class for block set types added by Blueprint's systems.
 */
public final class BlockSetTypeRegistryHelper {
	private static final HashMap<String, BlockSetType> BLOCK_SET_TYPES = new HashMap<>();

	public static void registerBlockSetTypes() {
		for (BlockSetType woodType : BLOCK_SET_TYPES.values()) BlockSetType.register(woodType);
	}

	/**
	 * Registers a {@link BlockSetType} to the {@link #BLOCK_SET_TYPES} map.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 */
	public static synchronized BlockSetType register(BlockSetType blockSetType) {
		BLOCK_SET_TYPES.put(blockSetType.name(), blockSetType);
		return blockSetType;
	}
}
