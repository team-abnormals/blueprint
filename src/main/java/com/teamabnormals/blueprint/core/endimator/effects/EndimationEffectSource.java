package com.teamabnormals.blueprint.core.endimator.effects;

import com.teamabnormals.blueprint.core.endimator.effects.shaking.ShakeEndimationEffect;

/**
 * The interface for defining an object that can process {@link ConfiguredEndimationEffect} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
//TODO: Possibly restructure this?
public interface EndimationEffectSource {
	/**
	 * Processes a screen shaking effect from a {@link ShakeEndimationEffect.Config} instance.
	 *
	 * @param config A {@link ShakeEndimationEffect.Config} instance to use for determining the screen shaking effect's attributes.
	 */
	default void processShake(ShakeEndimationEffect.Config config) {
	}

	/**
	 * Process a custom {@link EndimationEffect} instance.
	 * <p>'custom' being not built-in.</p>
	 *
	 * @param effect A custom {@link EndimationEffect} instance to process.
	 * @param config A config object to use for determining the effect's attributes.
	 */
	default <C> void processCustomEffect(EndimationEffect<C> effect, C config) {
	}
}
