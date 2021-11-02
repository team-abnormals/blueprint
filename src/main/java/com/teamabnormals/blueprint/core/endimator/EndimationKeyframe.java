package com.teamabnormals.blueprint.core.endimator;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.ErrorableOptionalFieldCodec;
import com.teamabnormals.blueprint.core.endimator.interpolation.ConfiguredEndimationInterpolator;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationEasers;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationInterpolator;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationInterpolators;

/**
 * An immutable class representing a keyframe for an {@link Endimation}.
 * <p>This class implements {@link Comparable} to compare it against other {@link EndimationKeyframe}s.</p>
 * <p>Use {@link #CODEC} for serialization and deserialization of instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see ConfiguredEndimationInterpolator
 */
public final class EndimationKeyframe implements Comparable<EndimationKeyframe> {
	private static final ConfiguredEndimationInterpolator<?, ?> LINEAR = new ConfiguredEndimationInterpolator<>(EndimationInterpolators.LINEAR, Unit.INSTANCE, EndimationEasers.LINEAR);
	public static final Codec<EndimationKeyframe> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.FLOAT.fieldOf("time").forGetter((keyframe) -> keyframe.time),
				Codec.FLOAT.optionalFieldOf("x", 0.0F).forGetter((keyframe) -> keyframe.x),
				Codec.FLOAT.optionalFieldOf("y", 0.0F).forGetter((keyframe) -> keyframe.y),
				Codec.FLOAT.optionalFieldOf("z", 0.0F).forGetter((keyframe) -> keyframe.z),
				ErrorableOptionalFieldCodec.errorableOptional("interpolation", ConfiguredEndimationInterpolator.CODEC, LINEAR).forGetter(keyframe -> keyframe.interpolator)
		).apply(instance, EndimationKeyframe::new);
	});
	public final float time;
	public final float x, y, z;
	public final ConfiguredEndimationInterpolator<?, ?> interpolator;

	public EndimationKeyframe(float time, float x, float y, float z, ConfiguredEndimationInterpolator<?, ?> interpolator) {
		this.time = time;
		this.x = x;
		this.y = y;
		this.z = z;
		this.interpolator = interpolator;
	}

	/**
	 * Applies this keyframe's {@link #interpolator} to a {@link EndimationInterpolator.VecConsumer} instance.
	 *
	 * @param consumer      A {@link EndimationInterpolator.VecConsumer} to apply the {@link #interpolator} to.
	 * @param keyframes     An array of {@link EndimationKeyframe}s to use for the {@link #interpolator}.
	 * @param index         The index of the current {@link EndimationKeyframe}.
	 * @param keyframeCount The length of the array of {@link EndimationKeyframe}s.
	 * @param progress      A percentage of how far the current {@link EndimationKeyframe} is to being done. Should be between 0 and 1.
	 * @see Endimator#apply(Endimation, float, Endimator.ResetMode)
	 * @see ConfiguredEndimationInterpolator#apply(EndimationInterpolator.VecConsumer, EndimationKeyframe[], EndimationKeyframe, int, int, float)
	 */
	public void apply(EndimationInterpolator.VecConsumer consumer, EndimationKeyframe[] keyframes, int index, int keyframeCount, float progress) {
		this.interpolator.apply(consumer, keyframes, this, index, keyframeCount, progress);
	}

	@Override
	public int compareTo(EndimationKeyframe keyframe) {
		return Float.compare(this.time, keyframe.time);
	}
}
