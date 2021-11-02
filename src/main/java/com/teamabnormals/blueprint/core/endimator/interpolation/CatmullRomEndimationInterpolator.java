package com.teamabnormals.blueprint.core.endimator.interpolation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.endimator.EndimationKeyframe;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An {@link EndimationInterpolator} instance that interpolates using catmull-rom splines.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class CatmullRomEndimationInterpolator extends EndimationInterpolator<CatmullRomEndimationInterpolator.Config> {
	private static final Codec<Config> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Config.Parameterization.CODEC.optionalFieldOf("parameterization", Config.Parameterization.CENTRIPETAL).forGetter(config -> config.parameterization),
				Codec.floatRange(0.0F, 1.0F).optionalFieldOf("tension", 0.0F).forGetter(config -> config.tension)
		).apply(instance, Config::new);
	});
	private static final Config DEFAULT_CONFIG = new Config(Config.Parameterization.CENTRIPETAL, 0.0F);

	public CatmullRomEndimationInterpolator() {
		super(CODEC, DEFAULT_CONFIG);
	}

	//A simplified form of the catmull rom polynomial. See https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline
	private static float catmullRom(float p0, float p1, float p2, float p3, float progress, Function<Float, Float> knotPowFunction, float tension) {
		float t01 = knotPowFunction.apply(Mth.abs(p1 - p0));
		float t12 = knotPowFunction.apply(Mth.abs(p2 - p1));
		float t23 = knotPowFunction.apply(Mth.abs(p3 - p2));
		float tensionFactor = 1.0F - tension;
		float f1 = (p1 - p0) / t01;
		//It is possible for the distance between two values to be 0, leading to divisions by 0.
		//Defining NaNs produced here when the distance is 0 as 0 solves this issue. It is not expected for NaNs to appear from anywhere else in this method.
		if (Float.isNaN(f1)) {
			f1 = 0.0F;
		}
		float f2 = (p2 - p0) / (t01 + t12);
		if (Float.isNaN(f2)) {
			f2 = 0.0F;
		}
		float f3 = (p3 - p2) / t23;
		if (Float.isNaN(f3)) {
			f3 = 0.0F;
		}
		float f4 = (p3 - p1) / (t12 + t23);
		if (Float.isNaN(f4)) {
			f4 = 0.0F;
		}
		float p2Minusp1 = p2 - p1;
		float m1 = tensionFactor * (p2Minusp1 + t12 * (f1 - f2));
		float m2 = tensionFactor * (p2Minusp1 + t12 * (f3 - f4));
		float p1Minusp2 = p1 - p2;
		return ((2.0F * (p1Minusp2) + m1 + m2) * progress * progress * progress) + ((-3.0F * (p1Minusp2) - m1 - m1 - m2) * progress * progress) + (m1 * progress) + p1;
	}

	@Override
	public void apply(Config config, VecConsumer consumer, EndimationKeyframe[] keyframes, EndimationKeyframe keyframe, int index, int keyframeCount, float progress) {
		float prevX = 0.0F;
		float prevY = 0.0F;
		float prevZ = 0.0F;
		float prevPrevX = 0.0F;
		float prevPrevY = 0.0F;
		float prevPrevZ = 0.0F;
		if (index >= 2) {
			EndimationKeyframe prev = keyframes[index - 1];
			prevX = prev.x;
			prevY = prev.y;
			prevZ = prev.z;
			EndimationKeyframe prevPrev = keyframes[index - 2];
			prevPrevX = prevPrev.x;
			prevPrevY = prevPrev.y;
			prevPrevZ = prevPrev.z;
		} else if (index >= 1) {
			EndimationKeyframe prev = keyframes[index - 1];
			prevX = prevPrevX = prev.x;
			prevY = prevPrevY = prev.y;
			prevZ = prevPrevZ = prev.z;
		}
		float x = keyframe.x;
		float y = keyframe.y;
		float z = keyframe.z;
		float nextX = x;
		float nextY = y;
		float nextZ = z;
		if (index + 1 < keyframeCount) {
			EndimationKeyframe next = keyframes[index + 1];
			nextX = next.x;
			nextY = next.y;
			nextZ = next.z;
		}
		Function<Float, Float> knotPowFunction = config.parameterization.knotPowFunction;
		float tension = config.tension;
		consumer.accept(catmullRom(prevPrevX, prevX, x, nextX, progress, knotPowFunction, tension), catmullRom(prevPrevY, prevY, y, nextY, progress, knotPowFunction, tension), catmullRom(prevPrevZ, prevZ, z, nextZ, progress, knotPowFunction, tension));
	}

	public static record Config(Parameterization parameterization, float tension) {
		/**
		 * An enum representing the three main parameterization for catmull-rom splines.
		 * <p>See <a href="http://www.cemyuksel.com/research/catmullrom_param">Parameterization of Catmull-Rom Curves</a> for information about the differences between these parameterization types.</p>
		 *
		 * @author SmellyModder (Luke Tonon)
		 */
		public enum Parameterization implements StringRepresentable {
			UNIFORM("uniform", distance -> 1.0F),
			CENTRIPETAL("centripetal", Mth::sqrt),
			CHORDAL("chordal", distance -> distance);

			private static final Map<String, Parameterization> NAME_LOOKUP = Arrays.stream(Parameterization.values()).collect(Collectors.toMap(Parameterization::getSerializedName, (axis) -> axis));
			public static final Codec<Parameterization> CODEC = StringRepresentable.fromEnum(Parameterization::values, Parameterization::byName);
			private final String name;
			private final Function<Float, Float> knotPowFunction;

			Parameterization(String name, Function<Float, Float> knotPowFunction) {
				this.name = name;
				this.knotPowFunction = knotPowFunction;
			}

			@Nullable
			public static Parameterization byName(String name) {
				return NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
			}

			@Override
			public String getSerializedName() {
				return this.name;
			}
		}
	}
}
