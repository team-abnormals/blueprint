package com.teamabnormals.blueprint.core.endimator;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.ErrorableOptionalFieldCodec;
import com.teamabnormals.blueprint.core.endimator.interpolation.ConfiguredEndimationInterpolator;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationEasers;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationInterpolator;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationInterpolators;
import net.minecraft.Util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

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
	private static final Either<Transform, Pair<Transform, Transform>> DEFAULT_TRANSFORM = Either.left(new Transform(() -> 0.0F, () -> 0.0F, () -> 0.0F));
	private static final Codec<Vector3f> VECTOR_CODEC = Codec.FLOAT.listOf().comapFlatMap((floats) -> {
		return Util.fixedSize(floats, 3).map((vec) -> new Vector3f(vec.get(0), vec.get(1), vec.get(2)));
	}, (vec) -> Arrays.asList(vec.x(), vec.y(), vec.z())).stable();
	public static final Codec<Transform> PRE_POST_CODEC = VECTOR_CODEC.xmap(vec -> {
		return new Transform(vec::x, vec::y, vec::z);
	}, expression -> new Vector3f(expression.x.get(), expression.y.get(), expression.z.get()));
	public static final Codec<Pair<Transform, Transform>> PRE_AND_POST_CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				PRE_POST_CODEC.fieldOf("pre").forGetter(Pair::getFirst),
				PRE_POST_CODEC.fieldOf("post").forGetter(Pair::getSecond)
		).apply(instance, Pair::new);
	});
	public static final Codec<EndimationKeyframe> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.FLOAT.fieldOf("time").forGetter(keyframe -> keyframe.time),
				Codec.either(PRE_POST_CODEC, PRE_AND_POST_CODEC).optionalFieldOf("transform", DEFAULT_TRANSFORM).forGetter(keyframe -> {
					return Either.right(Pair.of(new Transform(keyframe.preX, keyframe.preY, keyframe.preZ), new Transform(keyframe.postX, keyframe.postY, keyframe.postZ)));
				}),
				ErrorableOptionalFieldCodec.errorableOptional("interpolation", ConfiguredEndimationInterpolator.CODEC, LINEAR).forGetter(keyframe -> keyframe.interpolator)
		).apply(instance, (Float time, Either<Transform, Pair<Transform, Transform>> either, ConfiguredEndimationInterpolator<?, ?> interpolator) -> {
			Optional<Transform> left = either.left();
			if (left.isPresent()) {
				return new EndimationKeyframe(time, left.get(), interpolator);
			}
			Pair<Transform, Transform> preAndPost = either.right().get();
			Transform pre = preAndPost.getFirst();
			Transform post = preAndPost.getSecond();
			return new EndimationKeyframe(time, pre.x, pre.y, pre.z, post.x, post.y, post.z, interpolator);
		});
	});
	public final float time;
	public final Supplier<Float> preX, preY, preZ;
	public final Supplier<Float> postX, postY, postZ;
	public final ConfiguredEndimationInterpolator<?, ?> interpolator;

	public EndimationKeyframe(float time, Supplier<Float> preX, Supplier<Float> preY, Supplier<Float> preZ, Supplier<Float> postX, Supplier<Float> postY, Supplier<Float> postZ, ConfiguredEndimationInterpolator<?, ?> interpolator) {
		this.time = time;
		this.preX = preX;
		this.preY = preY;
		this.preZ = preZ;
		this.postX = postX;
		this.postY = postY;
		this.postZ = postZ;
		this.interpolator = interpolator;
	}

	public EndimationKeyframe(float time, Transform transform, ConfiguredEndimationInterpolator<?, ?> interpolator) {
		this.time = time;
		this.preX = this.postX = transform.x;
		this.preY = this.postY = transform.y;
		this.preZ = this.postZ = transform.z;
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

	/**
	 * A simple record class to store the three expressions that make up a transformation.
	 * <p>It's planned to replace the suppliers in this record with proper game expressions.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static record Transform(Supplier<Float> x, Supplier<Float> y, Supplier<Float> z) {
	}
}
