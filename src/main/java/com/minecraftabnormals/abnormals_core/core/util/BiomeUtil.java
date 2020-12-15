package com.minecraftabnormals.abnormals_core.core.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedList;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BiomeUtil {
	private static final Map<RegistryKey<Biome>, WeightedList<RegistryKey<Biome>>> hillBiomeMap = new HashMap<>();

	public static void addHillBiome(RegistryKey<Biome> biome, Pair<RegistryKey<Biome>, Integer>... hills) {
		for (Pair<RegistryKey<Biome>, Integer> hill : hills)
			hillBiomeMap.computeIfAbsent(biome, (k) -> new WeightedList()).func_226313_a_(hill.getFirst(), hill.getSecond());
	}

	public static RegistryKey<Biome> getHillBiome(RegistryKey<Biome> biome) {
		if (hillBiomeMap.containsKey(biome)) {
			Random random = new Random();
			return hillBiomeMap.get(biome).func_226318_b_(random);
		}
		return null;
	}

	public static boolean isBiome(ResourceLocation biome, RegistryKey<?>... biomes) {
		for (RegistryKey<?> key : biomes)
			if (key.getLocation().equals(biome))
				return true;
		return false;
	}
}
