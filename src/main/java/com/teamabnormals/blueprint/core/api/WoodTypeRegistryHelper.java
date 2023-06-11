package com.teamabnormals.blueprint.core.api;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

/**
 * Manager class for wood types added by Blueprint's systems.
 */
public final class WoodTypeRegistryHelper {
	private static final HashMap<String, WoodType> WOOD_TYPES = new HashMap<>();

	@OnlyIn(Dist.CLIENT)
	public static void setupAtlas() {
		for (WoodType woodType : WOOD_TYPES.values()) Sheets.addWoodType(woodType);
	}

	public static void registerWoodTypes() {
		for (WoodType woodType : WOOD_TYPES.values()) WoodType.register(woodType);
	}

	/**
	 * Registers a {@link WoodType} to the {@link #WOOD_TYPES} map.
     * <p>This method is safe to call during parallel mod loading.</p>
	 */
	public static synchronized WoodType registerWoodType(WoodType woodType) {
		WOOD_TYPES.put(woodType.name(), woodType);
		return woodType;
	}
}
