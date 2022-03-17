package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseRouterWithOnlyNoises;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	public static final ResourceKey<DensityFunction> DEFAULT_MODDEDNESS = ResourceKey.create(Registry.DENSITY_FUNCTION_REGISTRY, new ResourceLocation(Blueprint.MOD_ID, "moddedness/default"));
	public static final Codec<ModdedBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter((modded) -> modded.biomes),
				RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter((modded) -> modded.noiseParameters),
				RegistryOps.retrieveRegistry(Registry.DENSITY_FUNCTION_REGISTRY).forGetter((modded) -> modded.densityFunctions),
				BiomeSource.CODEC.fieldOf("original_biome_source").forGetter(modded -> modded.originalSource),
				NoiseSettings.CODEC.fieldOf("noise_settings").forGetter(modded -> modded.noiseSettings),
				Codec.LONG.fieldOf("seed").forGetter(modded -> modded.seed),
				Codec.BOOL.fieldOf("legacy_random_source").forGetter(modded -> modded.legacy),
				DensityFunction.HOLDER_HELPER_CODEC.fieldOf("moddedness").forGetter(modded -> modded.moddedness),
				WeightedBiomeSlices.CODEC.fieldOf("weighted_slices").forGetter(modded -> modded.weightedBiomeSlices)
		).apply(instance, ModdedBiomeSource::new);
	});
	private final Registry<Biome> biomes;
	private final Registry<NormalNoise.NoiseParameters> noiseParameters;
	private final Registry<DensityFunction> densityFunctions;
	private final BiomeSource originalSource;
	private final NoiseSettings noiseSettings;
	private final long seed;
	private final boolean legacy;
	private final DensityFunction moddedness;
	private final WeightedBiomeSlices weightedBiomeSlices;
	private final Biome originalSourceMarker;

	public ModdedBiomeSource(Registry<Biome> biomes, Registry<NormalNoise.NoiseParameters> noiseParameters, Registry<DensityFunction> densityFunctions, BiomeSource originalSource, NoiseSettings noiseSettings, long seed, boolean legacy, DensityFunction moddedness, WeightedBiomeSlices weightedBiomeSlices) {
		super(new ArrayList<>(weightedBiomeSlices.combinePossibleBiomes(originalSource.possibleBiomes(), biomes)));
		this.biomes = biomes;
		this.noiseParameters = noiseParameters;
		this.densityFunctions = densityFunctions;
		this.originalSource = originalSource;
		this.noiseSettings = noiseSettings;
		this.seed = seed;
		this.legacy = legacy;
		this.moddedness = visitModdednessDensityFunction(noiseSettings, seed, noiseParameters, legacy ? WorldgenRandom.Algorithm.LEGACY : WorldgenRandom.Algorithm.XOROSHIRO, moddedness);
		this.weightedBiomeSlices = weightedBiomeSlices;
		this.originalSourceMarker = biomes.getOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER.getKey());
	}

	//Vanilla privates tons of stuff here, so we will create a dummy noise router to visit the moddedness density function
	private static DensityFunction visitModdednessDensityFunction(NoiseSettings noiseSettings, long seed, Registry<NormalNoise.NoiseParameters> noiseParameters, WorldgenRandom.Algorithm algorithm, DensityFunction moddedness) {
		return NoiseRouterData.createNoiseRouter(noiseSettings, seed, noiseParameters, algorithm, new NoiseRouterWithOnlyNoises(moddedness, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero())).barrierNoise();
	}

	@Override
	public void addDebugInfo(List<String> strings, BlockPos pos, Climate.Sampler sampler) {
		BiomeSource original = this.originalSource;
		original.addDebugInfo(strings, pos, sampler);
		if (!(original instanceof ModdedBiomeSource))
			strings.add("Moddedness Slice: " + this.getSliceName(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getZ())));
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new ModdedBiomeSource(this.biomes, this.noiseParameters, this.densityFunctions, this.originalSource, this.noiseSettings, seed, this.legacy, this.moddedness, this.weightedBiomeSlices);
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
		Holder<Biome> biome = this.weightedBiomeSlices.getNoiseBiome(x, y, z, this.getModdedness(x, z), sampler, this.originalSource, this.biomes);
		return biome.value() == this.originalSourceMarker ? this.originalSource.getNoiseBiome(x, y, z, sampler) : biome;
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

	//We keep the y at 0 to avoid severely impacting performance for surface rules
	private float getModdedness(int x, int z) {
		return (float) this.moddedness.compute(new DensityFunction.SinglePointContext(QuartPos.toBlock(x), 0, QuartPos.toBlock(z)));
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
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return The additional possible biomes of the {@link #providers} merged with another set of possible biomes.
		 */
		public Set<Holder<Biome>> combinePossibleBiomes(Set<Holder<Biome>> possibleBiomes, Registry<Biome> registry) {
			Set<Holder<Biome>> biomes = new HashSet<>(possibleBiomes);
			for (BiomeUtil.ModdedBiomeProvider provider : this.providers) {
				biomes.addAll(provider.getAdditionalPossibleBiomes(registry));
			}
			return biomes;
		}

		/**
		 * Gets a holder of a noise {@link Biome} at a position.
		 *
		 * @param x          The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param y          The y pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param z          The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param moddedness A moddedness noise value between 0 and 1 inclusive to get a {@link BiomeUtil.ModdedBiomeProvider} instance within its range.
		 * @param sampler    A {@link Climate.Sampler} instance to sample {@link Climate.TargetPoint} instances.
		 * @param original   The original {@link BiomeSource} instance being modded.
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return A noise {@link Biome} at a position.
		 */
		public Holder<Biome> getNoiseBiome(int x, int y, int z, float moddedness, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			return this.getProvider(moddedness).getNoiseBiome(x, y, z, sampler, original, registry);
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
