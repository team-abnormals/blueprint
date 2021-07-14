package com.minecraftabnormals.abnormals_core.core.util;

import com.google.common.collect.Lists;
import com.minecraftabnormals.abnormals_core.common.world.gen.EdgeBiomeProvider;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * A utility class for biomes.
 *
 * @author bageldotjpg
 * @author SmellyModder (Luke Tonon)
 * @author ExpensiveKoala
 */
public final class BiomeUtil {
	private static final Map<RegistryKey<Biome>, WeightedNoiseList<RegistryKey<Biome>>> HILL_BIOME_MAP = new HashMap<>();
	private static final Map<OceanType, WeightedNoiseList<RegistryKey<Biome>>> OCEAN_BIOME_MAP = new HashMap<>();
	private static final Map<RegistryKey<Biome>, RegistryKey<Biome>> DEEP_OCEAN_BIOME_MAP = new HashMap<>();
	private static final Set<RegistryKey<Biome>> OCEAN_SET = new HashSet<>();
	private static final Set<RegistryKey<Biome>> SHALLOW_OCEAN_SET = new HashSet<>();
	private static final Map<RegistryKey<Biome>, PrioritizedNoiseList<EdgeBiomeProvider>> EDGE_BIOME_PROVIDER_MAP = new HashMap<>();
	private static final WeightedNoiseList<RegistryKey<Biome>> END_BIOMES = new WeightedNoiseList<>();
	private static final Set<ResourceLocation> CUSTOM_END_MUSIC_BIOMES = new HashSet<>();

	static {
		addEndBiome(Biomes.END_MIDLANDS, 15);
		addOceanBiome(OceanType.FROZEN, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, 15);
		addOceanBiome(OceanType.COLD, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, 15);
		addOceanBiome(OceanType.NORMAL, Biomes.OCEAN, Biomes.DEEP_OCEAN, 15);
		addOceanBiome(OceanType.LUKEWARM, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, 15);
		addOceanBiome(OceanType.WARM, Biomes.WARM_OCEAN, null, 15);
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
	 * @param key    A {@link Biome} {@link RegistryKey} to add.
	 * @param weight The weight for the {@link Biome}.
	 */
	public static synchronized void addEndBiome(RegistryKey<Biome> key, int weight) {
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
	 * Adds an ocean biome with its deep variant to generate with a given weight.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param type The {@link OceanType} to register the {@link Biome} to.
	 * @param biome The {@link Biome} {@link RegistryKey} to add.
	 * @param deep The {@link Biome} {@link RegistryKey} to add as the deep variant.
	 * @param weight The weight for the {@link Biome}.
	 */
	public static synchronized void addOceanBiome(OceanType type, RegistryKey<Biome> biome, @Nullable RegistryKey<Biome> deep, int weight) {
		OCEAN_BIOME_MAP.computeIfAbsent(type, (key) -> new WeightedNoiseList<>()).add(biome, weight);
		OCEAN_SET.add(biome);
		SHALLOW_OCEAN_SET.add(biome);
		if (deep != null) {
			DEEP_OCEAN_BIOME_MAP.put(biome, deep);
			OCEAN_SET.add(biome);
		}
	}
	
	/**
	 * Adds an {@link EdgeBiomeProvider} for a given {@link Biome} {@link RegistryKey}
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param key A {@link Biome} {@link RegistryKey} to add an {@link EdgeBiomeProvider} for.
	 * @param provider An {@link EdgeBiomeProvider} to use to determine the biome to border a certain biome.
	 */
	public static synchronized void addEdgeBiome(RegistryKey<Biome> key, EdgeBiomeProvider provider, Priority priority) {
		EDGE_BIOME_PROVIDER_MAP.computeIfAbsent(key, (k) -> new PrioritizedNoiseList<>()).add(provider, priority);
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
	 * Checks if a {@link ResourceLocation} belonging to a {@link Biome} should have the {@link Biome} plays its custom music in the end.
	 *
	 * @param biomeName The {@link ResourceLocation} belonging to a {@link Biome} to check.
	 * @return If a {@link ResourceLocation} belonging to a {@link Biome} should have the {@link Biome} plays its custom music in the end.
	 */
	public static boolean shouldPlayCustomEndMusic(ResourceLocation biomeName) {
		return CUSTOM_END_MUSIC_BIOMES.contains(biomeName);
	}
	
	/**
	 * Gets a random ocean biome for a given {@link OceanType} and {@link INoiseRandom}.
	 *
	 * @param type An {@link OceanType} to categorize the ocean temperature.
	 * @param random An {@link INoiseRandom} to randomly pick the ocean variant.
	 * @return A random ocean biome for a given {@link OceanType} and {@link INoiseRandom}.
	 */
	public static RegistryKey<Biome> getOceanBiome(OceanType type, INoiseRandom random) {
		return OCEAN_BIOME_MAP.getOrDefault(type, new WeightedNoiseList<>()).get(random);
	}
	
	/**
	 * Get the corresponding deep ocean variant of a {@link Biome} {@link RegistryKey}.
	 *
	 * @param oceanBiome The {@link Biome} {@link RegistryKey} to use to get the deep ocean variant.
	 * @return Null if no deep variant has been registered, otherwise the deep variant.
	 */
	@Nullable
	public static RegistryKey<Biome> getDeepOceanBiome(RegistryKey<Biome> oceanBiome) {
		return DEEP_OCEAN_BIOME_MAP.get(oceanBiome);
	}
	
	/**
	 * Check if a {@link Biome} {@link RegistryKey} is an ocean {@link Biome}.
	 *
	 * @param biome The {@link Biome} {@link RegistryKey} to check.
	 * @return If a {@link Biome} {@link RegistryKey} is registered as an ocean {@link Biome}.
	 */
	public static boolean isOceanBiome(RegistryKey<Biome> biome) {
		return OCEAN_SET.contains(biome);
	}
	
	/**
	 * Check if a {@link Biome} {@link RegistryKey} is an ocean {@link Biome}, but also not registered as a deep variant.
	 *
	 * @param biome The {@link Biome} {@link RegistryKey} to check.
	 * @return If a {@link Biome} {@link RegistryKey} is registered as an ocean {@link Biome}, but not as a deep variant.
	 */
	public static boolean isShallowOceanBiome(RegistryKey<Biome> biome) {
		return SHALLOW_OCEAN_SET.contains(biome);
	}
	
	/**
	 * Get the {@link Biome} {@link RegistryKey} from the registered {@link EdgeBiomeProvider}s.
	 *
	 * @param biome A {@link Biome} {@link RegistryKey} to retrieve the corresponding edge biome of.
	 * @param random The {@link INoiseRandom} to get the value randomly with.
	 * @param northBiome The {@link Biome} {@link RegistryKey} to the north.
	 * @param westBiome The {@link Biome} {@link RegistryKey} to the west.
	 * @param southBiome The {@link Biome} {@link RegistryKey} to the south.
	 * @param eastBiome The {@link Biome} {@link RegistryKey} to the east.
	 * @return The {@link Biome} {@link RegistryKey}, or null if no {@link EdgeBiomeProvider} returns a {@link Biome} {@link RegistryKey}.
	 */
	@Nullable
	public static RegistryKey<Biome> getEdgeBiome(RegistryKey<Biome> biome, INoiseRandom random, RegistryKey<Biome> northBiome, RegistryKey<Biome> westBiome, RegistryKey<Biome> southBiome, RegistryKey<Biome> eastBiome) {
		PrioritizedNoiseList<EdgeBiomeProvider> edgeBiomeProviderList = EDGE_BIOME_PROVIDER_MAP.get(biome);
		if (edgeBiomeProviderList != null) {
			Pair<EdgeBiomeProvider, RegistryKey<Biome>> pair = edgeBiomeProviderList.getWithCallback(random, edgeBiomeProvider -> edgeBiomeProvider.getEdgeBiome(random, northBiome, westBiome, southBiome, eastBiome));
			if (pair != null) {
				return pair.getSecond();
			}
		}
		return null;
	}
	
	/**
	 * Get the {@link Biome} id given a {@link Biome} {@link RegistryKey}.
	 * @param biome The {@link Biome} {@link RegistryKey} to get the id of.
	 * @return The id of the provided {@link Biome} {@link RegistryKey}.
	 */
	@SuppressWarnings("deprecation")
	public static int getId(@Nonnull RegistryKey<Biome> biome) {
		return WorldGenRegistries.BIOME.getId(WorldGenRegistries.BIOME.getValueForKey(biome));
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
		 * Gets a random entry using a given {@link INoiseRandom} without the use of a callback function.
		 *
		 * @param random A {@link INoiseRandom} to select a random entry.
		 * @return A random entry from the given {@link INoiseRandom}.
		 */
		@Nullable
		public T get(INoiseRandom random) {
			Pair<T, Object> pair = this.getWithCallback(random, o -> DUMMY_CALLBACK);
			return pair != null ? pair.getFirst() : null;
		}
		
		/**
		 * Gets a random entry using a given {@link INoiseRandom} validated through a callback function.
		 *
		 * @param random            A {@link INoiseRandom} to select a random entry.
		 * @param callbackProcessor A callback function to validate a selected entry and return an additional value associated with it.
		 * @return A {@link Pair} containing a random entry from the given {@link INoiseRandom} and a value associated with the entry.
		 */
		@Nullable
		public <C> Pair<T, C> getWithCallback(INoiseRandom random, Function<T, C> callbackProcessor) {
			for (List<T> list : this.priorityListMap.values()) {
				int size = list.size();
				if (size > 0) {
					List<T> copy = new ArrayList<>(list);
					while (size > 0) {
						int index = random.random(size);
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
	
	/**
	 * The 5 different ocean types that generate in the world.
	 */
	public enum OceanType {
		WARM, LUKEWARM, FROZEN, COLD, NORMAL
	}
	
	/**
	 * An enum representing 5 different levels of priority.
	 */
	public enum Priority {
		HIGHEST, HIGH, NORMAL, LOW, LOWEST
	}
}
