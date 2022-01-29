package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
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
				RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((modded) -> modded.biomes),
				RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter((modded) -> modded.noises),
				BiomeSource.CODEC.fieldOf("original_biome_source").forGetter(modded -> modded.originalSource),
				Codec.LONG.fieldOf("seed").forGetter(modded -> modded.seed),
				Codec.BOOL.fieldOf("legacy_random_source").forGetter(modded -> modded.legacy),
				Codec.BOOL.fieldOf("large_biomes").forGetter(modded -> modded.largeBiomes),
				WeightedBiomeSlices.CODEC.fieldOf("weighted_slices").forGetter(modded -> modded.weightedBiomeSlices)
		).apply(instance, ModdedBiomeSource::new);
	});
	private final Registry<Biome> biomes;
	private final Registry<NormalNoise.NoiseParameters> noises;
	private final BiomeSource originalSource;
	private final long seed;
	private final boolean legacy;
	private final boolean largeBiomes;
	private final NormalNoise moddednessNoise;
	private final NormalNoise offsetNoise;
	private final WeightedBiomeSlices weightedBiomeSlices;
	private final Biome originalSourceMarker;

	public ModdedBiomeSource(Registry<Biome> biomes, Registry<NormalNoise.NoiseParameters> noises, BiomeSource originalSource, long seed, boolean legacy, boolean largeBiomes, WeightedBiomeSlices weightedBiomeSlices) {
		super(new ArrayList<>(weightedBiomeSlices.combinePossibleBiomes(originalSource.possibleBiomes())));
		this.biomes = biomes;
		this.noises = noises;
		this.originalSource = originalSource;
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
		this.originalSourceMarker = biomes.getOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER.getKey());
	}

	@Override
	public void addMultinoiseDebugInfo(List<String> strings, BlockPos pos, Climate.Sampler sampler) {
		BiomeSource original = this.originalSource;
		original.addMultinoiseDebugInfo(strings, pos, sampler);
		if (!(original instanceof ModdedBiomeSource))
			strings.add("Moddedness Slice: " + this.getSliceName(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getZ())));
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new ModdedBiomeSource(this.biomes, this.noises, this.originalSource, seed, this.legacy, this.largeBiomes, this.weightedBiomeSlices);
	}

	@Override
	public Biome getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
		Biome biome = this.weightedBiomeSlices.getNoiseBiome(x, y, z, this.getModdedness(x, z), sampler, this.originalSource);
		return biome == this.originalSourceMarker ? this.originalSource.getNoiseBiome(x, y, z, sampler) : biome;
	}

	/**
	 * Gets the name of a modded provider at a horizontal position.
	 *
	 * @param x The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
	 * @param z The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
	 * @return The name of a modded provider at a horizontal position.
	 */
	public ResourceLocation getSliceName(int x, int z) {
		return this.weightedBiomeSlices.getSliceName(this.getModdedness(x, z));
	}

	private float getModdedness(int x, int z) {
		NormalNoise offsetNoise = this.offsetNoise;
		double shiftedX = x + offsetNoise.getValue(x, 0.0D, z) * 4.0D;
		double shiftedZ = z + offsetNoise.getValue(z, x, 0.0D) * 4.0D;
		return (float) this.moddednessNoise.getValue(shiftedX, 0.0D, shiftedZ);
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
		 * @param moddedness A moddedness noise value between 0 and 1 inclusive to get a {@link BiomeUtil.ModdedBiomeProvider} instance within its range.
		 * @param sampler    A {@link Climate.Sampler} instance to sample {@link Climate.TargetPoint} instances.
		 * @param original   The original {@link BiomeSource} instance being modded.
		 * @return A noise {@link Biome} at a position.
		 */
		public Biome getNoiseBiome(int x, int y, int z, float moddedness, Climate.Sampler sampler, BiomeSource original) {
			return this.getProvider(moddedness).getNoiseBiome(x, y, z, sampler, original);
		}

		/**
		 * Gets the name of a provider at a horizontal position.
		 *
		 * @param moddedness A moddedness noise value between 0 and 1 inclusive to get a {@link BiomeUtil.ModdedBiomeProvider} instance within its range.
		 * @return The name of a provider at a horizontal position.
		 */
		public ResourceLocation getSliceName(float moddedness) {
			return this.getProvider(moddedness).getName();
		}

		private BiomeUtil.ModdedBiomeProvider getProvider(float moddedness) {
			float[] providerThresholds = this.providerThresholds;
			int length = providerThresholds.length;
			for (int i = 0; i < length; i++) {
				if (providerThresholds[i] >= moddedness) {
					return this.providers[i];
				}
			}
			return this.providers[length - 1];
		}
	}
}
