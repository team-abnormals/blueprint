package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.*;
import java.util.stream.Stream;

/**
 * A {@link BiomeSource} subclass that wraps another {@link BiomeSource} instance and overlays its biomes with sliced modded biome providers.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see WeightedBiomeSlices
 * @see BiomeUtil.ModdedBiomeProvider
 */
public final class ModdedBiomeSource extends BiomeSource {
	public static final ResourceKey<NormalNoise.NoiseParameters> MODDEDNESS = ResourceKey.create(Registry.NOISE_REGISTRY, new ResourceLocation(Blueprint.MOD_ID, "moddedness"));
	public static final ResourceKey<NormalNoise.NoiseParameters> MODDEDNESS_LARGE = ResourceKey.create(Registry.NOISE_REGISTRY, new ResourceLocation(Blueprint.MOD_ID, "moddedness_large"));
	public static final Codec<ModdedBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter((modded) -> modded.noises),
				BiomeSource.CODEC.fieldOf("biome_source").forGetter(modded -> modded.biomeSource),
				Codec.LONG.fieldOf("seed").forGetter(modded -> modded.seed),
				Codec.BOOL.fieldOf("legacy_random_source").forGetter(modded -> modded.legacy),
				Codec.BOOL.fieldOf("large_biomes").forGetter(modded -> modded.largeBiomes),
				WeightedBiomeSlices.CODEC.fieldOf("weighted_slices").forGetter(modded -> modded.weightedBiomeSlices)
		).apply(instance, ModdedBiomeSource::new);
	});
	private final Registry<NormalNoise.NoiseParameters> noises;
	private final BiomeSource biomeSource;
	private final long seed;
	private final boolean legacy;
	private final boolean largeBiomes;
	private final NormalNoise moddednessNoise;
	private final NormalNoise offsetNoise;
	private final WeightedBiomeSlices weightedBiomeSlices;

	public ModdedBiomeSource(Registry<NormalNoise.NoiseParameters> noises, BiomeSource source, long seed, boolean legacy, boolean largeBiomes, WeightedBiomeSlices weightedBiomeSlices) {
		super(new ArrayList<>(weightedBiomeSlices.combinePossibleBiomes(source.possibleBiomes())));
		this.noises = noises;
		this.biomeSource = source;
		this.seed = seed;
		this.legacy = legacy;
		this.largeBiomes = largeBiomes;
		if (legacy) {
			PositionalRandomFactory positionalRandomFactory = WorldgenRandom.Algorithm.LEGACY.newInstance(seed).forkPositional();
			this.moddednessNoise = Noises.instantiate(noises, positionalRandomFactory, largeBiomes ? MODDEDNESS_LARGE : MODDEDNESS);
			this.offsetNoise = NormalNoise.create(positionalRandomFactory.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0D));
		} else {
			PositionalRandomFactory positionalRandomFactory = WorldgenRandom.Algorithm.XOROSHIRO.newInstance(seed).forkPositional();
			this.moddednessNoise = Noises.instantiate(noises, positionalRandomFactory, largeBiomes ? MODDEDNESS_LARGE : MODDEDNESS);
			this.offsetNoise = Noises.instantiate(noises, positionalRandomFactory, Noises.SHIFT);
		}
		this.weightedBiomeSlices = weightedBiomeSlices;
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new ModdedBiomeSource(this.noises, this.biomeSource, seed, this.legacy, this.largeBiomes, this.weightedBiomeSlices);
	}

	@Override
	public Biome getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
		NormalNoise offsetNoise = this.offsetNoise;
		double shiftedX = x + offsetNoise.getValue(x, 0.0D, z) * 4.0D;
		double shiftedZ = z + offsetNoise.getValue(z, x, 0.0D) * 4.0D;
		return this.weightedBiomeSlices.getNoiseBiome(x, y, z, (float) this.moddednessNoise.getValue(shiftedX, 0.0D, shiftedZ), sampler, this.biomeSource);
	}

	/**
	 * Handles the storing and processing of {@link BiomeUtil.ModdedBiomeProvider} instances.
	 * <p>A moddedness noise is used to slowly go over {@link BiomeUtil.ModdedBiomeProvider} instances that are sliced into weighted thresholds.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class WeightedBiomeSlices {
		public static final Codec<WeightedBiomeSlices> CODEC = BiomeUtil.ModdedBiomeProvider.CODEC.listOf().xmap(moddedBiomeSamplers -> new WeightedBiomeSlices(moddedBiomeSamplers.toArray(new BiomeUtil.ModdedBiomeProvider[0])), sliced -> List.of(sliced.providers));
		private final BiomeUtil.ModdedBiomeProvider[] providers;
		private final float[] providerThresholds;

		/**
		 * Constructs a new {@link WeightedBiomeSlices} instance.
		 *
		 * @param providers An array of {@link BiomeUtil.ModdedBiomeProvider} instances to use.
		 */
		public WeightedBiomeSlices(BiomeUtil.ModdedBiomeProvider... providers) {
			Arrays.sort(providers, Comparator.comparingInt(BiomeUtil.ModdedBiomeProvider::getWeight));
			this.providers = providers;
			int samplerCount = providers.length;
			this.providerThresholds = new float[samplerCount];
			float intervalProgress = -1.0F;
			float totalWeight = Stream.of(providers).map(BiomeUtil.ModdedBiomeProvider::getWeight).reduce(0, Integer::sum);
			for (int i = 0; i < samplerCount; i++) {
				this.providerThresholds[i] = (intervalProgress += (this.providers[i].getWeight() * 2) / totalWeight);
			}
		}

		/**
		 * Merges the additional possible biomes of the {@link #providers} with another set of possible biomes.
		 *
		 * @param possibleBiomes The possible biomes to merge with the additional possible biomes.
		 * @return The additional possible biomes of the {@link #providers} merged with another set of possible biomes.
		 */
		public Set<Biome> combinePossibleBiomes(Set<Biome> possibleBiomes) {
			HashSet<Biome> biomes = new HashSet<>(possibleBiomes);
			for (BiomeUtil.ModdedBiomeProvider provider : this.providers) {
				biomes.addAll(provider.getAdditionalPossibleBiomes());
			}
			return biomes;
		}

		/**
		 * Gets a noise {@link Biome} at a position.
		 *
		 * @param x          The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param y          The y pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param z          The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param moddedness A moddedness noise value between 0 and 1 inclusive to get a {@link ModdedBiomeSource} instance within its range.
		 * @param sampler    A {@link Climate.Sampler} instance to sample {@link Climate.TargetPoint} instances.
		 * @param original   The original {@link BiomeSource} instance being modded.
		 * @return A noise {@link Biome} at a position.
		 */
		public Biome getNoiseBiome(int x, int y, int z, float moddedness, Climate.Sampler sampler, BiomeSource original) {
			float[] providerThresholds = this.providerThresholds;
			int length = providerThresholds.length;
			for (int i = 0; i < length; i++) {
				if (providerThresholds[i] >= moddedness) {
					return this.providers[i].getNoiseBiome(x, y, z, sampler, original);
				}
			}
			return this.providers[length - 1].getNoiseBiome(x, y, z, sampler, original);
		}
	}
}
