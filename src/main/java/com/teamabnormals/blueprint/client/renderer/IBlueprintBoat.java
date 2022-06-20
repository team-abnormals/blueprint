package com.teamabnormals.blueprint.client.renderer;

import net.minecraft.resources.ResourceLocation;

/**
 * The interface for getting the texture of a Blueprint boat.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface IBlueprintBoat {
	/**
	 * Gets the {@link ResourceLocation} instance of the texture for the boat.
	 *
	 * @return The {@link ResourceLocation} instance of the texture for the boat.
	 */
	ResourceLocation getTexture();
}
