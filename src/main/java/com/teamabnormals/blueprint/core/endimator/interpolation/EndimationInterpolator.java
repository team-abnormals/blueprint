package com.teamabnormals.blueprint.core.endimator.interpolation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.endimator.Endimation;
import com.teamabnormals.blueprint.core.endimator.EndimationKeyframe;

import static com.teamabnormals.blueprint.common.codec.ErrorableOptionalFieldCodec.errorableOptional;

/**
 * An abstract class representing a configurable interpolator - used to apply progressed dimensional values from {@link EndimationKeyframe} instances.
 *
 * @param <C> The type of config object the interpolator will use to configure the applying of values.
 * @author SmellyModder (Luke Tonon)
 */
public abstract class EndimationInterpolator<C> {
	private final Codec<ConfiguredEndimationInterpolator<C, EndimationInterpolator<C>>> codec;

	protected EndimationInterpolator(Codec<C> codec, C defaultConfig) {
		this.codec = RecordCodecBuilder.create((instance) -> {
			return instance.group(
					errorableOptional("config", codec, defaultConfig).forGetter(ConfiguredEndimationInterpolator::getConfig),
					errorableOptional("easing", EndimationEasers.REGISTRY, EndimationEasers.LINEAR).forGetter(ConfiguredEndimationInterpolator::getEaser)
			).apply(instance, (config, easer) -> new ConfiguredEndimationInterpolator<>(this, config, easer));
		});
	}

	/**
	 * An abstract method that handles how the interpolator applies the progressed dimensional values from {@link EndimationKeyframe} instances.
	 *
	 * @param config        A config object of type {@code C} to configure the applying of the values.
	 * @param consumer      A {@link VecConsumer} to receive the values.
	 * @param keyframes     An array of {@link EndimationKeyframe} instances to use for relative frame reference.
	 * @param keyframe      The current {@link EndimationKeyframe}.
	 * @param index         The index of the current {@link EndimationKeyframe}.
	 * @param keyframeCount The length of the array of {@link EndimationKeyframe} instances.
	 * @param progress      A percentage of how far the current {@link EndimationKeyframe} is to being done. Should be between 0 and 1.
	 * @see com.teamabnormals.blueprint.core.endimator.Endimator#apply(Endimation, float, com.teamabnormals.blueprint.core.endimator.Endimator.ResetMode)
	 * @see EndimationKeyframe#apply(VecConsumer, EndimationKeyframe[], int, int, float)
	 */
	public abstract void apply(C config, VecConsumer consumer, EndimationKeyframe[] keyframes, EndimationKeyframe keyframe, int index, int keyframeCount, float progress);

	/**
	 * Gets the {@link #codec} used for serialization and deserialization of {@link ConfiguredEndimationInterpolator} instances for this interpolator.
	 *
	 * @return The {@link #codec} used for serialization and deserialization of {@link ConfiguredEndimationInterpolator} instances for this interpolator.
	 */
	public Codec<ConfiguredEndimationInterpolator<C, EndimationInterpolator<C>>> getCodec() {
		return this.codec;
	}

	/**
	 * A consumer-like interface that accepts three-dimensional vector values.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	@FunctionalInterface
	public interface VecConsumer {
		/**
		 * Accepts three-dimensional vector values and performs an action.
		 *
		 * @param x The x value.
		 * @param y The y value.
		 * @param z The z value.
		 */
		void accept(float x, float y, float z);
	}
}
