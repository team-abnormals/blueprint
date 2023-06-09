package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A fairly simple Math utility class.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class MathUtil {
	/**
	 * Gets the center of an {@link AABB} multiplied by a given multiplier.
	 *
	 * @param bb          An {@link AABB} to get the center for.
	 * @param xMultiplier A multiplier for the x bounds.
	 * @param yMultiplier A multiplier for the y bounds.
	 * @param zMultiplier A multiplier for the z bounds.
	 * @return The center of an {@link AABB} multiplied by a given multiplier.
	 */
	public static Vec3 getCenterAdjusted(AABB bb, double xMultiplier, double yMultiplier, double zMultiplier) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * xMultiplier, bb.minY + (bb.maxY - bb.minY) * yMultiplier, bb.minZ + (bb.maxZ - bb.minZ) * zMultiplier);
	}

	/**
	 * Gets the distance between two 2D points.
	 *
	 * @param x1 The first x point.
	 * @param y1 The first y point.
	 * @param x2 The second x point.
	 * @param y2 The second y point.
	 * @return The distance between two 2D points.
	 */
	public static double distanceBetweenPoints2d(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	/**
	 * Flips a number to its negative form with a 50/50 chance.
	 *
	 * @param value A value to flip.
	 * @param rand  A {@link RandomSource} to get the next boolean from.
	 * @return A number that will be in its negative form 50% of the time.
	 */
	public static double makeNegativeRandomly(double value, RandomSource rand) {
		return rand.nextBoolean() ? -value : value;
	}

	/**
	 * Flips a number to its negative form with a given chance.
	 *
	 * @param value  A value to flip.
	 * @param rand   A {@link RandomSource} to get the next float from.
	 * @param chance The chance to flip the value.
	 * @return A number that will be in its negative form when the next float is less than the given chance.
	 */
	public static double makeNegativeRandomlyWithFavoritism(double value, RandomSource rand, float chance) {
		return rand.nextFloat() < chance ? -value : value;
	}

	/**
	 * Finds the smallest integer in an int array.
	 *
	 * @param array An array to find the smallest value in.
	 * @return The smallest integer in the given int array.
	 */
	public static int getLowestValueInIntArray(int[] array) {
		int currentLowest = Integer.MAX_VALUE;
		for (int value : array) {
			if (value <= currentLowest) {
				currentLowest = value;
			}
		}
		return currentLowest;
	}

	/**
	 * Gets the brightness level based on a given darkness level.
	 *
	 * @param light The darkness level.
	 * @return The brightness level based on the given darkness level.
	 */
	public static int getBrightLightForLight(int light) {
		return light >= 175 ? light : Math.abs(light - 240);
	}

	/**
	 * Approximates the inverse square root of a given float.
	 *
	 * @param x A float to get the inverse square root for.
	 * @return The approximate inverse square root of a given float.
	 */
	public static float invSqrt(float x) {
		float halfX = 0.5F * x;
		int i = Float.floatToIntBits(x);
		i = 0x5f3759df - (i >> 1);
		x = Float.intBitsToFloat(i);
		x *= (1.5F - halfX * x * x);
		return x;
	}

	/**
	 * Interpolates two {@link Vec3}s.
	 *
	 * @param prev    The previous vector.
	 * @param current The current vector.
	 * @param ptc     The partial ticks to use for scaling the difference vector.
	 * @return A {@link Vec3} containing the interpolated values of the given vectors.
	 * @see Mth#lerp(float, float, float)
	 */
	public static Vec3 lerp(Vec3 prev, Vec3 current, float ptc) {
		return prev.add(current.subtract(prev).scale(ptc));
	}

	/**
	 * An functional interface representing a computable equation with respect to x.
	 */
	@FunctionalInterface
	public interface Equation {
		double compute(double x);
	}

	/**
	 * Sourced from <a href=https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline>https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline</a>
	 * <p>Catmull Rom-Spline implementation that initializes points in an arch-like formation.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class CatmullRomSpline {
		private final Vec3[] points;
		private final float alpha;

		public CatmullRomSpline(Vec3[] points, SplineType splineType) {
			this.points = points;
			this.alpha = splineType.getAlpha();
		}

		private static double multiplyPoints(double point1, double point2, double multiplier1, double multiplier2) {
			return point1 * multiplier1 + point2 * multiplier2;
		}

		private static float computeT(Vec3 point1, Vec3 point2, float alpha, float offset) {
			return (float) Math.pow(point2.subtract(point1).length(), alpha) + offset;
		}

		/**
		 * Gets a position on the spline from a given progress percentage.
		 *
		 * @return A {@link BlockPos} on the spline for a given progress.
		 */
		public BlockPos interpolate(float progress) {
			Vec3[] points = this.points;
			float sections = points.length - 3;
			int currentPoint = (int) Math.min(Math.floor(progress * sections), sections - 1);
			float alpha = this.alpha;

			Vec3 point0 = points[currentPoint];
			Vec3 point1 = points[currentPoint + 1];
			float t0 = computeT(point0, point1, alpha, 0.0F);

			Vec3 point2 = points[currentPoint + 2];
			Vec3 point3 = points[currentPoint + 3];
			float t1 = computeT(point1, point2, alpha, t0);

			float t = t0 + (progress * sections - (float) currentPoint) * (t1 - t0);

			double A1multiplier1 = (t0 - t) / t0;
			double A1multiplier2 = t / t0;
			double A1x = multiplyPoints(point0.x, point1.x, A1multiplier1, A1multiplier2);
			double A1y = multiplyPoints(point0.y, point1.y, A1multiplier1, A1multiplier2);
			double A1z = multiplyPoints(point0.z, point1.z, A1multiplier1, A1multiplier2);

			double A2Denominator = t1 - t0;
			double A2Multiplier1 = (t1 - t) / A2Denominator;
			double A2Multiplier2 = (t - t0) / A2Denominator;
			double A2x = multiplyPoints(point1.x, point2.x, A2Multiplier1, A2Multiplier2);
			double A2y = multiplyPoints(point1.y, point2.y, A2Multiplier1, A2Multiplier2);
			double A2z = multiplyPoints(point1.z, point2.z, A2Multiplier1, A2Multiplier2);

			float t3 = computeT(point2, point3, alpha, t1);
			double A3Denominator = t3 - t1;
			double A3Multiplier1 = (t3 - t) / A3Denominator;
			double A3Multiplier2 = (t - t1) / A3Denominator;
			double A3x = multiplyPoints(point2.x, point3.x, A3Multiplier1, A3Multiplier2);
			double A3y = multiplyPoints(point2.y, point3.y, A3Multiplier1, A3Multiplier2);
			double A3z = multiplyPoints(point2.z, point3.z, A3Multiplier1, A3Multiplier2);

			double B1Multiplier1 = (t1 - t) / t1;
			double B1Multiplier2 = t / t1;
			double B1x = multiplyPoints(A1x, A2x, B1Multiplier1, B1Multiplier2);
			double B1y = multiplyPoints(A1y, A2y, B1Multiplier1, B1Multiplier2);
			double B1z = multiplyPoints(A1z, A2z, B1Multiplier1, B1Multiplier2);

			double B2Denominator = t3 - t0;
			double B2Multiplier1 = (t3 - t) / B2Denominator;
			double B2Multiplier2 = (t - t0) / B2Denominator;
			double B2x = multiplyPoints(A2x, A3x, B2Multiplier1, B2Multiplier2);
			double B2y = multiplyPoints(A2y, A3y, B2Multiplier1, B2Multiplier2);
			double B2z = multiplyPoints(A2z, A3z, B2Multiplier1, B2Multiplier2);

			double CDenominator = t1 - t0;
			double CMultiplier1 = (t1 - t) / CDenominator;
			double CMultiplier2 = (t - t0) / CDenominator;
			return new BlockPos(Mth.floor(multiplyPoints(B1x, B2x, CMultiplier1, CMultiplier2)), Mth.floor(multiplyPoints(B1y, B2y, CMultiplier1, CMultiplier2)), Mth.floor(multiplyPoints(B1z, B2z, CMultiplier1, CMultiplier2)));
		}

		/**
		 * The different types of splines and their corresponding alpha values.
		 */
		public enum SplineType {
			STANDARD(0.0F),
			CENTRIPETAL(0.5F),
			CHORDAL(1.0F);

			private final float alpha;

			SplineType(float alpha) {
				this.alpha = alpha;
			}

			public float getAlpha() {
				return this.alpha;
			}
		}
	}
}