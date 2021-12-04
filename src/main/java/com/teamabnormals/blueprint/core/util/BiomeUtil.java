package com.teamabnormals.blueprint.core.util;

import com.google.common.collect.ImmutableList;
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
import java.util.function.Supplier;

/**
 * A utility class for biomes.
 *
 * @author bageldotjpg
 * @author SmellyModder (Luke Tonon)
 * @author ExpensiveKoala
 */
public final class BiomeUtil {
	private static final List<Pair<Climate.Parameter, Pair<ResourceKey<Biome>, ResourceKey<Biome>>>> OCEAN_BIOMES = new ArrayList<>();
	private static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> END_BIOMES = new ArrayList<>();
	private static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> NETHER_BIOMES = new ArrayList<>();
	private static final Set<ResourceLocation> CUSTOM_END_MUSIC_BIOMES = new HashSet<>();

	static {
		addEndBiome(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), Biomes.THE_VOID);
	}

	/**
	 * Adds hill variants to the given {@link Biome} {@link ResourceKey}.
	 * <p>Each entry is given a weight to allow variants to appear more often than others.</p>
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param biome A {@link Biome} {@link ResourceKey} to add hill variants to.
	 * @param hills An array of pairs containing a {@link Biome} {@link ResourceKey} and a weight.
	 */
	//@SafeVarargs
	//public static synchronized void addHillBiome(ResourceKey<Biome> biome, Pair<ResourceKey<Biome>, Integer>... hills) {
	//    WeightedNoiseList<ResourceKey<Biome>> list = HILL_BIOME_MAP.computeIfAbsent(biome, (key) -> new WeightedNoiseList<>());
	//    for (Pair<ResourceKey<Biome>, Integer> hill : hills) {
	//        list.add(hill.getFirst(), hill.getSecond());
	//    }
	//}

	/**
	 * Adds an end biome with a given {@link Climate.ParameterPoint} instance for the biome's climate properties.
	 *
	 * @param point A {@link Climate.ParameterPoint} instance to use for the biome.
	 * @param key   The {@link ResourceKey} of the biome.
	 */
	public static synchronized void addEndBiome(Climate.ParameterPoint point, ResourceKey<Biome> key) {
		END_BIOMES.add(Pair.of(point, key));
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
//	@Nullable
//	public static ResourceKey<Biome> getHillBiome(ResourceKey<Biome> biome, Context random) {
//		WeightedNoiseList<ResourceKey<Biome>> list = HILL_BIOME_MAP.get(biome);
//		return list != null ? list.get(random) : null;
//	}

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
	 * Gets a new {@link Climate.ParameterList} instance containing the {@link #END_BIOMES} list.
	 *
	 * @return A new {@link Climate.ParameterList} instance containing the {@link #END_BIOMES} list.
	 */
	public static Climate.ParameterList<ResourceKey<Biome>> getEndBiomes() {
		return new Climate.ParameterList<>(END_BIOMES);
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
}
