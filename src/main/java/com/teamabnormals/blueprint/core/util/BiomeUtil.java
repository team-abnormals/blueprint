package com.teamabnormals.blueprint.core.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A utility class for biomes.
 *
 * @author bageldotjpg
 * @author SmellyModder (Luke Tonon)
 * @author ExpensiveKoala
 */
public final class BiomeUtil {
	private static final Map<ResourceKey<Biome>, WeightedNoiseList<ResourceKey<Biome>>> HILL_BIOME_MAP = new HashMap<>();
	private static final List<Pair<Climate.Parameter, Pair<ResourceKey<Biome>, ResourceKey<Biome>>>> OCEAN_BIOMES = new ArrayList<>();
	private static final WeightedNoiseList<ResourceKey<Biome>> END_BIOMES = new WeightedNoiseList<>();
	private static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> NETHER_BIOMES = new ArrayList<>();
	private static final Set<ResourceLocation> CUSTOM_END_MUSIC_BIOMES = new HashSet<>();

	static {
		addEndBiome(Biomes.END_MIDLANDS, 15);
	}

	/**
	 * Adds hill variants to the given {@link Biome} {@link ResourceKey}.
	 * <p>Each entry is given a weight to allow variants to appear more often than others.</p>
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param biome A {@link Biome} {@link ResourceKey} to add hill variants to.
	 * @param hills An array of pairs containing a {@link Biome} {@link ResourceKey} and a weight.
	 */
	@SafeVarargs
	public static synchronized void addHillBiome(ResourceKey<Biome> biome, Pair<ResourceKey<Biome>, Integer>... hills) {
		WeightedNoiseList<ResourceKey<Biome>> list = HILL_BIOME_MAP.computeIfAbsent(biome, (key) -> new WeightedNoiseList<>());
		for (Pair<ResourceKey<Biome>, Integer> hill : hills) {
			list.add(hill.getFirst(), hill.getSecond());
		}
	}

	/**
	 * Adds an end biome to generate with a given weight.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param key    A {@link Biome} {@link ResourceKey} to add.
	 * @param weight The weight for the {@link Biome}.
	 */
	public static synchronized void addEndBiome(ResourceKey<Biome> key, int weight) {
		END_BIOMES.add(key, weight);
	}

	/**
	 * Marks the {@link ResourceLocation} belonging to a {@link Biome} to have it play its music in the end.
	 * <p>The music for biomes in the end is hardcoded, and this gets around that.</p>
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param biomeName The {@link ResourceLocation} belonging to a {@link Biome} to have it play its music in the end.
	 */
	public static synchronized void markEndBiomeCustomMusic(ResourceLocation biomeName) {
		CUSTOM_END_MUSIC_BIOMES.add(biomeName);
	}

	/**
	 * Adds an ocean biome with its deep variant to generate with a given {@link Climate.Parameter} temperature.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param temperature The {@link Climate.Parameter} temperature to have the biome generate in.
	 * @param biome       The {@link Biome} {@link ResourceKey} to add.
	 * @param deep        The {@link Biome} {@link ResourceKey} to add as the deep variant.
	 */
	public static synchronized void addOceanBiome(Climate.Parameter temperature, ResourceKey<Biome> biome, @Nullable ResourceKey<Biome> deep) {
		OCEAN_BIOMES.add(Pair.of(temperature, Pair.of(biome, deep)));
	}

	/**
	 * Adds a biome to generate in the Nether with specific a {@link Climate.ParameterPoint}.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param point The {@link Climate.ParameterPoint} instance to use for biome's generation attributes.
	 * @param biome The {@link ResourceKey} of the {@link Biome} to use.
	 */
	public static synchronized void addNetherBiome(Climate.ParameterPoint point, ResourceKey<Biome> biome) {
		NETHER_BIOMES.add(Pair.of(point, biome));
	}

	/**
	 * Gets a random hill variant for a given {@link Biome} {@link ResourceKey}.
	 *
	 * @param biome  A {@link Biome} {@link ResourceKey} to get a random hill variant for.
	 * @param random An {@link Context} to randomly pick the hill variant.
	 * @return A random hill variant for a given {@link Biome} {@link ResourceKey}, or null if there are no hill variants for the given {@link Biome} {@link ResourceKey}.
	 */
	@Nullable
	public static ResourceKey<Biome> getHillBiome(ResourceKey<Biome> biome, Context random) {
		WeightedNoiseList<ResourceKey<Biome>> list = HILL_BIOME_MAP.get(biome);
		return list != null ? list.get(random) : null;
	}

	/**
	 * Gets the list of registered modded ocean biomes.
	 * <p>This method is only used internally.</p>
	 *
	 * @return The list of registered modded ocean biomes.
	 */
	public static List<Pair<Climate.Parameter, Pair<ResourceKey<Biome>, ResourceKey<Biome>>>> getOceanBiomes() {
		return OCEAN_BIOMES;
	}

	/**
	 * Gets a random end biome for a given {@link Context}.
	 *
	 * @param random An {@link Context} to use for randomly picking an end biome.
	 * @return A random end biome for a given {@link Context}.
	 */
	public static ResourceKey<Biome> getEndBiome(Context random) {
		return END_BIOMES.get(random);
	}

	/**
	 * Checks if a {@link ResourceLocation} belonging to a {@link Biome} should have the {@link Biome} plays its custom music in the end.
	 *
	 * @param biomeName The {@link ResourceLocation} belonging to a {@link Biome} to check.
	 * @return If a {@link ResourceLocation} belonging to a {@link Biome} should have the {@link Biome} plays its custom music in the end.
	 */
	public static boolean shouldPlayCustomEndMusic(ResourceLocation biomeName) {
		return CUSTOM_END_MUSIC_BIOMES.contains(biomeName);
	}

	/**
	 * Gets an {@link ImmutableList} containing base (vanilla) nether biome data and modded nether biome data.
	 * <p>This method is only ever called once when the {@link MultiNoiseBiomeSource.Preset#NETHER} field is loaded.</p>
	 *
	 * @param baseBiomes The base list containing nether biome data to merge into one {@link ImmutableList} with modded nether biome data.
	 * @param registry   A {@link Biome} {@link Registry} to lookup the {@link Biome}s.
	 * @return An {@link ImmutableList} containing base (vanilla) nether biome data and modded nether biome data.
	 */
	public static List<Pair<Climate.ParameterPoint, Supplier<Biome>>> getModifiedNetherBiomes(List<Pair<Climate.ParameterPoint, Supplier<Biome>>> baseBiomes, Registry<Biome> registry) {
		ImmutableList.Builder<Pair<Climate.ParameterPoint, Supplier<Biome>>> builder = new ImmutableList.Builder<>();
		builder.addAll(baseBiomes);
		NETHER_BIOMES.forEach(resourceKeyClimateParametersPair -> {
			ResourceKey<Biome> biomeResourceKey = resourceKeyClimateParametersPair.getSecond();
			builder.add(Pair.of(resourceKeyClimateParametersPair.getFirst(), () -> registry.getOrThrow(biomeResourceKey)));
		});
		return builder.build();
	}

	/**
	 * Get the {@link Biome} id given a {@link Biome} {@link ResourceKey}.
	 *
	 * @param biome The {@link Biome} {@link ResourceKey} to get the id of.
	 * @return The id of the provided {@link Biome} {@link ResourceKey}.
	 */
	@SuppressWarnings("deprecation")
	public static int getId(@Nonnull ResourceKey<Biome> biome) {
		return BuiltinRegistries.BIOME.getId(BuiltinRegistries.BIOME.get(biome));
	}

	/**
	 * An enum representing 5 different levels of priority.
	 */
	public enum Priority {
		HIGHEST, HIGH, NORMAL, LOW, LOWEST
	}

	/**
	 * A weighted list that randomly picks entries based on a given {@link Context}.
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
		 * Gets a value randomly based on a given {@link Context}.
		 *
		 * @param random A {@link Context} to get the value randomly with.
		 * @return A random value based on a given {@link Context}.
		 */
		@Nonnull
		public T get(Context random) {
			Iterator<Pair<T, Integer>> iterator = this.entries.iterator();
			T value;
			int randomTotal = random.nextRandom(this.totalWeight);
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

	/**
	 * A list-like class that has its entries prioritized but also capable of being chosen randomly.
	 * <p>The prioritization is done through mapping out lists and then continuously selecting a random object in the current highest priority list until that object gets validated through a callback {@link Function}.</p>
	 * <p>This is useful in scenarios where objects are to be chosen randomly, with some objects having a guarantee to be chosen before other objects.</p>
	 *
	 * @param <T> The type of values to store.
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class PrioritizedNoiseList<T> {
		private static final Object DUMMY_CALLBACK = new Object();
		private final EnumMap<Priority, List<T>> priorityListMap = new EnumMap<>(Priority.class);

		/**
		 * Adds an object to a {@link List} mapped to a given {@link Priority}.
		 *
		 * @param value    An object to add.
		 * @param priority The {@link Priority} of this entry.
		 */
		public void add(T value, Priority priority) {
			this.priorityListMap.computeIfAbsent(priority, priority1 -> new ArrayList<>()).add(value);
		}

		/**
		 * Gets a random entry using a given {@link Context} without the use of a callback function.
		 *
		 * @param random A {@link Context} to select a random entry.
		 * @return A random entry from the given {@link Context}.
		 */
		@Nullable
		public T get(Context random) {
			Pair<T, Object> pair = this.getWithCallback(random, o -> DUMMY_CALLBACK);
			return pair != null ? pair.getFirst() : null;
		}

		/**
		 * Gets a random entry using a given {@link Context} validated through a callback function.
		 *
		 * @param random            A {@link Context} to select a random entry.
		 * @param callbackProcessor A callback function to validate a selected entry and return an additional value associated with it.
		 * @return A {@link Pair} containing a random entry from the given {@link Context} and a value associated with the entry.
		 */
		@Nullable
		public <C> Pair<T, C> getWithCallback(Context random, Function<T, C> callbackProcessor) {
			for (List<T> list : this.priorityListMap.values()) {
				int size = list.size();
				if (size > 0) {
					List<T> copy = new ArrayList<>(list);
					while (size > 0) {
						int index = random.nextRandom(size);
						T picked = copy.get(index);
						C callback = callbackProcessor.apply(picked);
						if (callback != null) {
							return Pair.of(picked, callback);
						} else {
							copy.remove(index);
							size--;
						}
					}
				}
			}
			return null;
		}

		/**
		 * Gets the internal {@link EnumMap} used to prioritize the entries in lists.
		 *
		 * @return The internal {@link EnumMap} used to prioritize the entries in lists.
		 */
		@Nonnull
		public EnumMap<Priority, List<T>> getPriorityListMap() {
			return this.priorityListMap;
		}
	}
}
