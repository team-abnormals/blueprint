package com.teamabnormals.blueprint.core.endimator.interpolation;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.endimator.EndimationKeyframe;

import java.util.function.Function;

/**
 * A class that represents a configured {@link EndimationInterpolator} by storing an {@link EndimationInterpolator} instance, a config object, and an easing function.
 * <p>Use the {@link #CODEC} field for serializing and deserializing instances of this class.</p>
 *
 * @param <C> The type of config to use.
 * @param <I> The type of {@link EndimationInterpolator} to configure.
 * @author SmellyModder (Luke Tonon)
 * @see EndimationInterpolator
 */
public final class ConfiguredEndimationInterpolator<C, I extends EndimationInterpolator<C>> {
	public static final Codec<ConfiguredEndimationInterpolator<?, ?>> CODEC = EndimationInterpolators.REGISTRY.dispatch(configured -> {
		return configured.interpolator;
	}, EndimationInterpolator::getCodec);
	private final I interpolator;
	private final C config;
	private final Function<Float, Float> easer;

	public ConfiguredEndimationInterpolator(I interpolator, C config, Function<Float, Float> easer) {
		this.interpolator = interpolator;
		this.config = config;
		this.easer = easer;
	}

	/**
	 * Handles the {@link #interpolator}'s applying, using the internal {@link #config} and {@link #easer}.
	 *
	 * @param consumer      A {@link EndimationInterpolator.VecConsumer} to receive the values.
	 * @param keyframes     An array of {@link EndimationKeyframe}s to use for relative frame reference.
	 * @param keyframe      The current {@link EndimationKeyframe}.
	 * @param index         The index of the current {@link EndimationKeyframe}.
	 * @param keyframeCount The length of the array of {@link EndimationKeyframe}s.
	 * @param progress      A percentage of how far the current {@link EndimationKeyframe} is to being done. Should be between 0 and 1.
	 * @see EndimationInterpolator#apply(Object, EndimationInterpolator.VecConsumer, EndimationKeyframe[], EndimationKeyframe, int, int, float)
	 */
	public void apply(EndimationInterpolator.VecConsumer consumer, EndimationKeyframe[] keyframes, EndimationKeyframe keyframe, int index, int keyframeCount, float progress) {
		this.interpolator.apply(this.config, consumer, keyframes, keyframe, index, keyframeCount, this.easer.apply(progress));
	}

	/**
	 * Gets the {@link #interpolator}.
	 *
	 * @return The {@link #interpolator}.
	 */
	public I getInterpolator() {
		return this.interpolator;
	}

	/**
	 * Gets the {@link #config} for the {@link #interpolator}.
	 *
	 * @return The {@link #config} for the {@link #interpolator}.
	 */
	public C getConfig() {
		return this.config;
	}

	/**
	 * Gets the {@link #easer} used for computing the progress in {@link #apply(EndimationInterpolator.VecConsumer, EndimationKeyframe[], EndimationKeyframe, int, int, float)}.
	 *
	 * @return The {@link #easer} used for computing the progress in {@link #apply(EndimationInterpolator.VecConsumer, EndimationKeyframe[], EndimationKeyframe, int, int, float)}.
	 */
	public Function<Float, Float> getEaser() {
		return this.easer;
	}
}
