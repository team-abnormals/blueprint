package com.teamabnormals.abnormals_core.core.utils;

import java.util.Random;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public class MathUtils {

	public interface Equation {
		public double compute(double theta);
	}
	
	public static double makeNegativeRandomly(double value, Random rand) {
		return rand.nextBoolean() ? -value : value;
	}
	
	public static double makeNegativeRandomlyWithFavoritism(double value, Random rand, float negativeChance) {
		return rand.nextFloat() < negativeChance ? -value : value;
	}
	
	public static int getBrightLightForLight(int light) {
    	int maxLight = 240;
    	int newLight = light >= 175 ? light : Math.abs(light - maxLight);
    	return newLight;
    }
	
}