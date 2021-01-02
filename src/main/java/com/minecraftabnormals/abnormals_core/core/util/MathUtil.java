package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

/**
 * A fairly simple Math utility class.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class MathUtil {

	/**
	 * Gets the center of an {@link AxisAlignedBB} multiplied by a given multiplier.
	 *
	 * @param bb          An {@link AxisAlignedBB} to get the center for.
	 * @param xMultiplier A multiplier for the x bounds.
	 * @param yMultiplier A multiplier for the y bounds.
	 * @param zMultiplier A multiplier for the z bounds.
	 * @return The center of an {@link AxisAlignedBB} multiplied by a given multiplier.
	 */
	public static Vector3d getCenterAdjusted(AxisAlignedBB bb, double xMultiplier, double yMultiplier, double zMultiplier) {
		return new Vector3d(bb.minX + (bb.maxX - bb.minX) * xMultiplier, bb.minY + (bb.maxY - bb.minY) * yMultiplier, bb.minZ + (bb.maxZ - bb.minZ) * zMultiplier);
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
		return MathHelper.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	/**
	 * Flips a number to its negative form with a 50/50 chance.
	 *
	 * @param value A value to flip.
	 * @param rand  A {@link Random} to get the next boolean from.
	 * @return A number that will be in its negative form 50% of the time.
	 */
	public static double makeNegativeRandomly(double value, Random rand) {
		return rand.nextBoolean() ? -value : value;
	}

	/**
	 * Flips a number to its negative form with a given chance.
	 *
	 * @param value  A value to flip.
	 * @param rand   A {@link Random} to get the next float from.
	 * @param chance The chance to flip the value.
	 * @return A number that will be in its negative form when the next float is less than the given chance.
	 */
	public static double makeNegativeRandomlyWithFavoritism(double value, Random rand, float chance) {
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
	 * Interpolates two {@link Vector3d}s.
	 *
	 * @param prev    The previous vector.
	 * @param current The current vector.
	 * @param ptc     The partial ticks to use for scaling the difference vector.
	 * @return A {@link Vector3d} containing the interpolated values of the given vectors.
	 * @see MathHelper#lerp(float, float, float)
	 */
	public static Vector3d lerp(Vector3d prev, Vector3d current, float ptc) {
		return prev.add(current.subtract(prev).scale(ptc));
	}

	/**
	 * An functional interface representing a computable equation with respect to x.
	 */
	@FunctionalInterface
	public interface Equation {
		double compute(double x);
	}

}