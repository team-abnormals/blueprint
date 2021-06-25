package com.minecraftabnormals.abnormals_core.core.util;

import com.google.common.collect.Lists;
import com.minecraftabnormals.abnormals_core.common.world.gen.EdgeBiomeProvider;
import com.minecraftabnormals.abnormals_core.common.world.gen.ocean.OceanType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A utility class for biomes.
 *
 * @author bageldotjpg
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeUtil {
	private static final Map<RegistryKey<Biome>, WeightedNoiseList<RegistryKey<Biome>>> HILL_BIOME_MAP = new HashMap<>();
	private static final Map<OceanType, WeightedNoiseList<RegistryKey<Biome>>> OCEAN_BIOME_MAP = new HashMap<>();
	private static final Map<RegistryKey<Biome>, RegistryKey<Biome>> DEEP_OCEAN_BIOME_MAP = new HashMap<>();
	private static final Map<RegistryKey<Biome>, EdgeBiomeProvider> EDGE_BIOME_PROVIDER_MAP = new HashMap<>();
	private static final WeightedNoiseList<RegistryKey<Biome>> END_BIOMES = new WeightedNoiseList<>();
	private static final Set<ResourceLocation> CUSTOM_END_MUSIC_BIOMES = new HashSet<>();

	static {
		addEndBiome(Biomes.END_MIDLANDS, 15);
		addOceanBiome(OceanType.FROZEN, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, 15);
		addOceanBiome(OceanType.COLD, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, 15);
		addOceanBiome(OceanType.NORMAL, Biomes.OCEAN, Biomes.DEEP_OCEAN, 15);
		addOceanBiome(OceanType.LUKEWARM, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, 15);
		addOceanBiome(OceanType.WARM, Biomes.WARM_OCEAN, 15);
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
	 * Adds an ocean biome to generate with a given weight.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param type The {@link OceanType} to register the {@link Biome} to.
	 * @param biome A {@link Biome} {@link RegistryKey} to add.
	 * @param weight The weight for the {@link Biome}.
	 */
	public static synchronized void addOceanBiome(OceanType type, RegistryKey<Biome> biome, int weight) {
		addOceanBiome(type, biome, null, weight);
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
		WeightedNoiseList<RegistryKey<Biome>> list = OCEAN_BIOME_MAP.computeIfAbsent(type, (key) -> new WeightedNoiseList<>());
		list.add(biome, weight);
		if(deep != null) {
			DEEP_OCEAN_BIOME_MAP.put(biome, deep);
		}
	}
	
	/**
	 * Adds an {@link EdgeBiomeProvider} for a given {@link Biome} {@link RegistryKey}
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param key A {@link Biome} {@link RegistryKey} to add an {@link EdgeBiomeProvider} for.
	 * @param provider An {@link EdgeBiomeProvider} to use to determine the biome to border a certain biome.
	 */
	public static synchronized void addEdgeBiome(RegistryKey<Biome> key, EdgeBiomeProvider provider) {
		EDGE_BIOME_PROVIDER_MAP.put(key, provider);
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
		for (WeightedNoiseList<RegistryKey<Biome>> list : OCEAN_BIOME_MAP.values()) {
			if (list.getEntries().stream().anyMatch((pair) -> pair.getFirst().equals(biome))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if a {@link Biome} {@link RegistryKey} is an ocean {@link Biome}, but also not registered as a deep variant.
	 *
	 * @param biome The {@link Biome} {@link RegistryKey} to check.
	 * @return If a {@link Biome} {@link RegistryKey} is registered as an ocean {@link Biome}, but not as a deep variant.
	 */
	public static boolean isShallowOceanBiome(RegistryKey<Biome> biome) {
		for (WeightedNoiseList<RegistryKey<Biome>> list : OCEAN_BIOME_MAP.values()) {
			if (list.getEntries().stream()
			.filter((pair) -> DEEP_OCEAN_BIOME_MAP.values().stream().noneMatch((key) -> key.equals(pair.getFirst())))
			.anyMatch((pair) -> pair.getFirst().equals(biome))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the {@link EdgeBiomeProvider} for the {@link Biome} {@link RegistryKey} if one exists.
	 *
	 * @param biome A {@link Biome} {@link RegistryKey} to retrieve the corresponding {@link EdgeBiomeProvider}
	 * @return The {@link EdgeBiomeProvider} corresponding to the {@link Biome} {@link RegistryKey}, or null if no {@link EdgeBiomeProvider} has been added.
	 */
	@Nullable
	public static EdgeBiomeProvider getEdgeBiomeProvider(RegistryKey<Biome> biome) {
		return EDGE_BIOME_PROVIDER_MAP.get(biome);
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
