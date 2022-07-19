package com.teamabnormals.blueprint.client.screen.splash;

import java.util.List;

/**
 * <p>This interface is used for accessing the list of splashes in {@link net.minecraft.client.resources.SplashManager}.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see com.teamabnormals.blueprint.core.mixin.client.SplashManagerMixin
 */
public interface SplashManagerAccessor {
	/**
	 * Gets the list of splashes in {@link net.minecraft.client.resources.SplashManager}.
	 *
	 * @return The list of splashes in {@link net.minecraft.client.resources.SplashManager}.
	 */
	List<String> getSplashes();
}
