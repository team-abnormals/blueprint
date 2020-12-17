package com.minecraftabnormals.abnormals_core.core.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.WeightedList;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author - bageldotjpg
 */
public final class BiomeUtil {
	private static final Map<RegistryKey<Biome>, WeightedList<RegistryKey<Biome>>> HILL_BIOME_MAP = new HashMap<>();
	private static final Random RANDOM = new Random();

	/**
	 * Adds hill variants to the given biomes
	 * Each entry is given a weight to allow variants to appear more often than others
	 */
	public static void addHillBiome(RegistryKey<Biome> biome, Pair<RegistryKey<Biome>, Integer>... hills) {
		for (Pair<RegistryKey<Biome>, Integer> hill : hills)
			HILL_BIOME_MAP.computeIfAbsent(biome, (k) -> new WeightedList<>()).func_226313_a_(hill.getFirst(), hill.getSecond());
	}

	/**
	 * Gets a hill variant for the given biome
	 */
	public static RegistryKey<Biome> getHillBiome(RegistryKey<Biome> biome) {
		if (HILL_BIOME_MAP.containsKey(biome)) {
			return HILL_BIOME_MAP.get(biome).func_226318_b_(RANDOM);
		}
		return null;
	}
}
