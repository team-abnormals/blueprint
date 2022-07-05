package com.teamabnormals.blueprint.core.endimator;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.ErrorableOptionalFieldCodec;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationEasers;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationInterpolator;
import com.teamabnormals.blueprint.core.endimator.interpolation.InterpolationType;
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
 * @see EndimationInterpolator
 */
public final class EndimationKeyframe implements Comparable<EndimationKeyframe> {
	public static final EndimationInterpolator LINEAR = new EndimationInterpolator(InterpolationType.LINEAR, EndimationEasers.LINEAR);
	public static final EndimationInterpolator CATMULL_ROM = new EndimationInterpolator(InterpolationType.CATMULL_ROM, EndimationEasers.LINEAR);
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
					Transform pre = new Transform(keyframe.preX, keyframe.preY, keyframe.preZ);
					Transform post = new Transform(keyframe.postX, keyframe.postY, keyframe.postZ);
					return pre.equals(post) ? Either.left(post) : Either.right(Pair.of(pre, post));
				}),
				ErrorableOptionalFieldCodec.errorableOptional("interpolation", EndimationInterpolator.CODEC, LINEAR).forGetter(keyframe -> keyframe.interpolator)
		).apply(instance, (time, singleTransformOrPreAndPost, interpolator) -> {
			Optional<Transform> left = singleTransformOrPreAndPost.left();
			if (left.isPresent()) {
				return new EndimationKeyframe(time, left.get(), interpolator);
			}
			Pair<Transform, Transform> preAndPost = singleTransformOrPreAndPost.right().get();
			Transform pre = preAndPost.getFirst();
			Transform post = preAndPost.getSecond();
			return new EndimationKeyframe(time, pre.x, pre.y, pre.z, post.x, post.y, post.z, interpolator);
		});
	});
	public final float time;
	public final Supplier<Float> preX, preY, preZ;
	public final Supplier<Float> postX, postY, postZ;
	public final EndimationInterpolator interpolator;

	public EndimationKeyframe(float time, Supplier<Float> preX, Supplier<Float> preY, Supplier<Float> preZ, Supplier<Float> postX, Supplier<Float> postY, Supplier<Float> postZ, EndimationInterpolator interpolator) {
		this.time = time;
		this.preX = preX;
		this.preY = preY;
		this.preZ = preZ;
		this.postX = postX;
		this.postY = postY;
		this.postZ = postZ;
		this.interpolator = interpolator;
	}

	public EndimationKeyframe(float time, Transform transform, EndimationInterpolator interpolator) {
		this.time = time;
		this.preX = this.postX = transform.x;
		this.preY = this.postY = transform.y;
		this.preZ = this.postZ = transform.z;
		this.interpolator = interpolator;
	}

	/**
	 * Applies this keyframe's {@link #interpolator} to a {@link Vector3f} instance.
	 *
	 * @param vec3          A {@link Vector3f} instance to apply the {@link #interpolator} to.
	 * @param keyframes     An array of {@link EndimationKeyframe}s to use for the {@link #interpolator}.
	 * @param from          The {@link EndimationKeyframe} instance getting receded from.
	 * @param to            The {@link EndimationKeyframe} instance getting approached to.
	 * @param index         The index of the current {@link EndimationKeyframe}.
	 * @param keyframeCount The length of the array of {@link EndimationKeyframe}s.
	 * @param progress      A percentage of how far the current {@link EndimationKeyframe} is to being done. Should be between 0 and 1.
	 * @see Endimator#apply(Endimation, float, Endimator.ResetMode)
	 * @see EndimationInterpolator#apply(Vector3f, EndimationKeyframe[], EndimationKeyframe, EndimationKeyframe, int, int, float)
	 */
	public void apply(Vector3f vec3, EndimationKeyframe[] keyframes, EndimationKeyframe from, EndimationKeyframe to, int index, int keyframeCount, float progress) {
		this.interpolator.apply(vec3, keyframes, from, to, index, keyframeCount, progress);
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
	public static record Transform(Supplier<Float> x, Supplier<Float> y, Supplier<Float> z) {}
}
