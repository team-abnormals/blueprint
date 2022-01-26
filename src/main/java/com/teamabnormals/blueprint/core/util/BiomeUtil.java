package com.teamabnormals.blueprint.core.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
	private static final BasicRegistry<Codec<? extends ModdedBiomeProvider>> MODDED_PROVIDERS = new BasicRegistry<>();

	static {
		addEndBiome(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), Biomes.THE_VOID);
	}

	static {
		MODDED_PROVIDERS.register(new ResourceLocation(Blueprint.MOD_ID, "default"), DefaultModdedBiomeProvider.CODEC);
		MODDED_PROVIDERS.register(new ResourceLocation(Blueprint.MOD_ID, "multi_noise"), MultiNoiseModdedBiomeProvider.CODEC);
	}

	/**
	 * Registers a new {@link ModdedBiomeProvider} type that can be serialized and deserialized.
	 *
	 * @param name  A {@link ResourceLocation} name for the provider.
	 * @param codec A {@link Codec} to use for serializing and deserializing instances of the {@link ModdedBiomeProvider} type.
	 */
	public static synchronized void registerBiomeProvider(ResourceLocation name, Codec<? extends ModdedBiomeProvider> codec) {
		MODDED_PROVIDERS.register(name, codec);
	}

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
	@Deprecated
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
	@Deprecated
	public static synchronized void addNetherBiome(Climate.ParameterPoint point, ResourceKey<Biome> biome) {
		NETHER_BIOMES.add(Pair.of(point, biome));
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
	 * Gets a new {@link Climate.ParameterList} instance containing the {@link #END_BIOMES} list.
	 *
	 * @return A new {@link Climate.ParameterList} instance containing the {@link #END_BIOMES} list.
	 */
	@Deprecated
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
	@Deprecated
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
	 * The interface for weighted biome source slices used in the {@link com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource} class.
	 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource
	 */
	public interface ModdedBiomeProvider {
		Codec<ModdedBiomeProvider> CODEC = BiomeUtil.MODDED_PROVIDERS.dispatchStable(ModdedBiomeProvider::codec, Function.identity());

		/**
		 * Gets a noise {@link Biome} at a position in a modded slice.
		 *
		 * @param x        The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param y        The y pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param z        The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param sampler  A {@link Climate.Sampler} instance to sample {@link net.minecraft.world.level.biome.Climate.TargetPoint} instances.
		 * @param original The original {@link BiomeSource} instance that this provider is modding.
		 * @return A noise {@link Biome} at a position in a modded slice.
		 */
		Biome getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original);

		/**
		 * Gets a set of the additional possible biomes that this provider may have.
		 *
		 * @return A set of the additional possible biomes that this provider may have.
		 * @see com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource.WeightedBiomeSlices#combinePossibleBiomes(Set).
		 */
		Set<Biome> getAdditionalPossibleBiomes();

		/**
		 * Gets the weight of this provider.
		 * <p>Higher weights mean more common.</p>
		 *
		 * @return The weight of this provider.
		 */
		int getWeight();

		/**
		 * Gets a {@link Codec} instance for serializing and deserializing this provider.
		 *
		 * @return A {@link Codec} instance for serializing and deserializing this provider.
		 */
		Codec<? extends ModdedBiomeProvider> codec();
	}

	/**
	 * A simple {@link ModdedBiomeProvider} implementation that uses the original biome source's {@link BiomeSource#getNoiseBiome(int, int, int, Climate.Sampler)} method.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static record DefaultModdedBiomeProvider(int weight) implements ModdedBiomeProvider {
		public static final Codec<DefaultModdedBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					Codec.INT.fieldOf("weight").forGetter(provider -> provider.weight)
			).apply(instance, DefaultModdedBiomeProvider::new);
		});

		@Override
		public Biome getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original) {
			return original.getNoiseBiome(x, y, z, sampler);
		}

		@Override
		public int getWeight() {
			return this.weight;
		}

		@Override
		public Codec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}

		@Override
		public Set<Biome> getAdditionalPossibleBiomes() {
			return new HashSet<>(0);
		}
	}

	/**
	 * A {@link ModdedBiomeProvider} implementation that uses a {@link net.minecraft.world.level.biome.Climate.ParameterList} instance for selecting its biomes.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static record MultiNoiseModdedBiomeProvider(Climate.ParameterList<Supplier<Biome>> biomes, int weight) implements ModdedBiomeProvider {
		public static final Codec<MultiNoiseModdedBiomeProvider> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(
					ExtraCodecs.nonEmptyList(RecordCodecBuilder.<Pair<Climate.ParameterPoint, Supplier<Biome>>>create((p_187078_) -> {
						return p_187078_.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(p_187078_, Pair::of);
					}).listOf()).xmap(Climate.ParameterList::new, Climate.ParameterList::values).fieldOf("biomes").forGetter(sampler -> sampler.biomes),
					Codec.INT.fieldOf("weight").forGetter(MultiNoiseModdedBiomeProvider::getWeight)
			).apply(instance, MultiNoiseModdedBiomeProvider::new);
		});

		@SuppressWarnings("deprecation")
		@Override
		public Biome getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original) {
			return this.biomes.findValue(sampler.sample(x, y, z), () -> net.minecraft.data.worldgen.biome.Biomes.THE_VOID).get();
		}

		@Override
		public int getWeight() {
			return this.weight;
		}

		@Override
		public Codec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}

		@Override
		public Set<Biome> getAdditionalPossibleBiomes() {
			return this.biomes.values().stream().map(pair -> pair.getSecond().get()).collect(Collectors.toSet());
		}
	}
}
