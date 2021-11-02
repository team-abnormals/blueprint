package com.teamabnormals.blueprint.core.endimator.effects;

import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.Endimation;

/**
 * A class for handling the processing of {@link ConfiguredEndimationEffect} instances on {@link Endimatable} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationEffectHandler {
	private final EndimationEffectSource source;
	private int recentIndex;
	private float prevTime;

	public EndimationEffectHandler(EndimationEffectSource source) {
		this.source = source;
	}

	/**
	 * Updates this handler for a given {@link Endimation} on a given {@link Endimatable} instance at a given time.
	 *
	 * @param endimation An {@link Endimation} to process.
	 * @param time       The time (in seconds) the effects are getting processed at.
	 */
	public void update(Endimation endimation, float time) {
		if (this.prevTime > time) {
			this.reset();
		}
		this.prevTime = time;
		ConfiguredEndimationEffect<?, ?>[] effects = endimation.getEffects();
		int length = effects.length;
		int recentIndex = this.recentIndex;
		if (length > 0 && recentIndex < length) {
			EndimationEffectSource source = this.source;
			while (recentIndex < length) {
				ConfiguredEndimationEffect<?, ?> effect = effects[recentIndex];
				if (effect.getTime() <= time) {
					effect.process(source, time);
					recentIndex++;
				} else {
					break;
				}
			}
			this.recentIndex = recentIndex;
		}
	}

	/**
	 * Resets the handler's {@link #recentIndex} to 0.
	 * <p>This is necessary when a new effect cycle has begun.</p>
	 */
	public void reset() {
		this.recentIndex = 0;
	}
}
