package com.minecraftabnormals.abnormals_core.core.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A utility class for biomes.
 *
 * @author bageldotjpg
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeUtil {
	private static final Map<RegistryKey<Biome>, WeightedNoiseList<RegistryKey<Biome>>> HILL_BIOME_MAP = new HashMap<>();
	private static final WeightedNoiseList<RegistryKey<Biome>> END_BIOMES = new WeightedNoiseList<>();

	static {
		addEndBiome(Biomes.END_MIDLANDS, 15);
	}

	/**
	 * Adds hill variants to the given {@link Biome} {@link RegistryKey}.
	 * <p>Each entry is given a weight to allow variants to appear more often than others.</p>
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param biome A {@link Biome} {@link RegistryKey} to add hill variants to.
	 * @param hills An array of pairs containing a {@link Biome} {@link RegistryKey} and a weight.
	 */
	@SafeVarargs
	public static synchronized void addHillBiome(RegistryKey<Biome> biome, Pair<RegistryKey<Biome>, Integer>... hills) {
		WeightedNoiseList<RegistryKey<Biome>> list = HILL_BIOME_MAP.computeIfAbsent(biome, (key) -> new WeightedNoiseList<>());
		for (Pair<RegistryKey<Biome>, Integer> hill : hills) {
			list.add(hill.getFirst(), hill.getSecond());
		}
	}

	/**
	 * Adds an end biome to generate with a given weight.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param key A {@link Biome} {@link RegistryKey} to add.
	 * @param weight The weight for the {@link Biome}.
	 */
	public static synchronized void addEndBiome(RegistryKey<Biome> key, int weight) {
		END_BIOMES.add(key, weight);
	}

	/**
	 * Gets a random hill variant for a given {@link Biome} {@link RegistryKey}.
	 *
	 * @param biome  A {@link Biome} {@link RegistryKey} to get a random hill variant for.
	 * @param random An {@link INoiseRandom} to randomly pick the hill variant.
	 * @return A random hill variant for a given {@link Biome} {@link RegistryKey}, or null if there are no hill variants for the given {@link Biome} {@link RegistryKey}.
	 */
	@Nullable
	public static RegistryKey<Biome> getHillBiome(RegistryKey<Biome> biome, INoiseRandom random) {
		WeightedNoiseList<RegistryKey<Biome>> list = HILL_BIOME_MAP.get(biome);
		return list != null ? list.get(random) : null;
	}

	/**
	 * Gets a random end biome for a given {@link INoiseRandom}.
	 *
	 * @param random An {@link INoiseRandom} to use for randomly picking an end biome.
	 * @return A random end biome for a given {@link INoiseRandom}.
	 */
	public static RegistryKey<Biome> getEndBiome(INoiseRandom random) {
		return END_BIOMES.get(random);
	}

	/**
	 * A weighted list that randomly picks entries based on a given {@link INoiseRandom}.
	 *
	 * @param <T> The type of values to store.
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class WeightedNoiseList<T> {
		private final List<Pair<T, Integer>> entries = Lists.newArrayList();
		private int totalWeight;

		/**
		 * Adds a value with a given weight.
		 *
		 * @param value  A value to add.
		 * @param weight The weight of the value to add.
		 */
		public void add(@Nonnull T value, int weight) {
			this.totalWeight += weight;
			this.entries.add(Pair.of(value, weight));
		}

		/**
		 * Gets a value randomly based on a given {@link INoiseRandom}.
		 *
		 * @param random A {@link INoiseRandom} to get the value randomly with.
		 * @return A random value based on a given {@link INoiseRandom}.
		 */
		@Nonnull
		public T get(INoiseRandom random) {
			Iterator<Pair<T, Integer>> iterator = this.entries.iterator();
			T value;
			int randomTotal = random.random(this.totalWeight);
			do {
				Pair<T, Integer> entry = iterator.next();
				value = entry.getFirst();
				randomTotal -= entry.getSecond();
			}
			while (randomTotal >= 0);
			return value;
		}

		/**
		 * Gets this weighted list's {@link #entries}.
		 *
		 * @return This weighted list's {@link #entries}.
		 */
		@Nonnull
		public List<Pair<T, Integer>> getEntries() {
			return this.entries;
		}
	}
}
