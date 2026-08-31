package com.teamabnormals.blueprint.core.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.BlueprintExtraCodecs;
import com.teamabnormals.blueprint.common.codec.NullableFieldCodec;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.registry.BlueprintDensityFunctions.SinglePointCacheDensityFunction;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A utility class for biomes.
 *
 * @author bageldotjpg
 * @author SmellyModder (Luke Tonon)
 * @author ExpensiveKoala
 */
public final class BiomeUtil {
	private static final Set<ResourceKey<Biome>> CUSTOM_END_MUSIC_BIOMES = new HashSet<>();
	private static final BasicRegistry<MapCodec<? extends ModdedBiomeProvider>> MODDED_PROVIDERS = new BasicRegistry<>();
	public static final Codec<ResourceKey<Biome>> BIOME_KEY_CODEC = ResourceKey.codec(Registries.BIOME);

	static {
		MODDED_PROVIDERS.register(Blueprint.location("original"), BiomeUtil.OriginalModdedBiomeProvider.CODEC);
		MODDED_PROVIDERS.register(Blueprint.location("multi_noise"), BiomeUtil.MultiNoiseModdedBiomeProvider.CODEC);
		MODDED_PROVIDERS.register(Blueprint.location("overlay"), BiomeUtil.OverlayModdedBiomeProvider.CODEC);
		MODDED_PROVIDERS.register(Blueprint.location("biome_source"), BiomeUtil.BiomeSourceModdedBiomeProvider.CODEC);
	}

	/**
	 * Registers a new {@link ModdedBiomeProvider} type that can be serialized and deserialized.
	 *
	 * @param name  A {@link ResourceLocation} name for the provider.
	 * @param codec A {@link MapCodec} to use for serializing and deserializing instances of the {@link ModdedBiomeProvider} type.
	 */
	public static synchronized void registerBiomeProvider(ResourceLocation name, MapCodec<? extends ModdedBiomeProvider> codec) {
		MODDED_PROVIDERS.register(name, codec);
	}

	/**
	 * Marks the {@link ResourceKey} belonging to a {@link Biome} to have it play its music in the end.
	 * <p>The music for biomes in the end is hardcoded, and this gets around that.</p>
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param biomeName The {@link ResourceKey} belonging to a {@link Biome} to have it play its music in the end.
	 */
	public static synchronized void markEndBiomeCustomMusic(ResourceKey<Biome> biomeName) {
		CUSTOM_END_MUSIC_BIOMES.add(biomeName);
	}

	/**
	 * Checks if a {@link ResourceKey} belonging to a {@link Biome} should have the {@link Biome} plays its custom music in the end.
	 *
	 * @param biomeName The {@link ResourceKey} belonging to a {@link Biome} to check.
	 * @return If a {@link ResourceKey} belonging to a {@link Biome} should have the {@link Biome} plays its custom music in the end.
	 */
	public static boolean shouldPlayCustomEndMusic(ResourceKey<Biome> biomeName) {
		return CUSTOM_END_MUSIC_BIOMES.contains(biomeName);
	}

	/**
	 * A biome-optimized {@link DensityFunction.FunctionContext} that updates per change in block position.
	 * <p>Many modded biome providers may need access to the same climate parameters or original biome.</p>
	 * <p>This acts as a local-scope cache for commonly sampled biome conditions.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class ScopedDensityFunctionContext implements DensityFunction.FunctionContext {
		private Climate.Sampler optimizedSampler;
		// These are fields to avoid casting on the sampler's getters
		private SinglePointCacheDensityFunction temperature;
		private SinglePointCacheDensityFunction humidity;
		private SinglePointCacheDensityFunction continentalness;
		private SinglePointCacheDensityFunction erosion;
		private SinglePointCacheDensityFunction depth;
		private SinglePointCacheDensityFunction weirdness;
		private Holder<Biome> originalBiome;
		private Climate.TargetPoint targetPoint;
		private int blockX;
		private int blockY;
		private int blockZ;

		public void reset(Climate.Sampler sampler, int x, int y, int z) {
			// Some annoying bookkeeping, but the performance gains are worth it
			// Wrap sampler's density functions (when needed) for caching repeated samples on (x, y, z)
			if (this.optimizedSampler != null) {
				boolean needsNewSampler = false;
				if (this.temperature.base() != sampler.temperature()) {
					this.temperature = this.cached(sampler.temperature());
					needsNewSampler = true;
				} else this.temperature.reset();
				if (this.humidity.base() != sampler.humidity()) {
					this.humidity = this.cached(sampler.humidity());
					needsNewSampler = true;
				} else this.humidity.reset();
				if (this.continentalness.base() != sampler.continentalness()) {
					this.continentalness = this.cached(sampler.continentalness());
					needsNewSampler = true;
				} else this.continentalness.reset();
				if (this.erosion.base() != sampler.erosion()) {
					this.erosion = this.cached(sampler.erosion());
					needsNewSampler = true;
				} else this.erosion.reset();
				if (this.depth.base() != sampler.depth()) {
					this.depth = this.cached(sampler.depth());
					needsNewSampler = true;
				} else this.depth.reset();
				if (this.weirdness.base() != sampler.weirdness()) {
					this.weirdness = this.cached(sampler.weirdness());
					needsNewSampler = true;
				} else this.weirdness.reset();
				if (needsNewSampler)
					this.optimizedSampler = new Climate.Sampler(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, sampler.spawnTarget());
			} else {
				this.optimizedSampler = new Climate.Sampler(
					this.temperature = this.cached(sampler.temperature()),
					this.humidity = this.cached(sampler.humidity()),
					this.continentalness = this.cached(sampler.continentalness()),
					this.erosion = this.cached(sampler.erosion()),
					this.depth = this.cached(sampler.depth()),
					this.weirdness = this.cached(sampler.weirdness()),
					sampler.spawnTarget()
				);
			}
			this.originalBiome = null;
			this.targetPoint = null;
			this.blockX = QuartPos.toBlock(x);
			this.blockY = QuartPos.toBlock(y);
			this.blockZ = QuartPos.toBlock(z);
		}

		private SinglePointCacheDensityFunction cached(DensityFunction function) {
			return new SinglePointCacheDensityFunction(function, this);
		}

		/**
		 * Gets the cached "original" biome at (x, y, z).
		 *
		 * @param x        The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param y        The y pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param z        The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param original The original biome source to sample from.
		 * @return The "original" biome.
		 */
		public Holder<Biome> getOriginalBiome(int x, int y, int z, BiomeSource original) {
			if (this.originalBiome != null) return this.originalBiome;
			return this.originalBiome = original.getNoiseBiome(x, y, z, this.optimizedSampler);
		}

		/**
		 * Gets the cached {@link Climate.TargetPoint} at this context's position.
		 * <p>This should only be used if you need most or all of the climate parameters.</p>
		 *
		 * @return The cached {@link Climate.TargetPoint} instance.
		 */
		public Climate.TargetPoint getTargetPoint() {
			if (this.targetPoint != null) return this.targetPoint;
			Climate.Sampler sampler = this.optimizedSampler;
			return this.targetPoint = Climate.target(
				(float) sampler.temperature().compute(this),
				(float) sampler.humidity().compute(this),
				(float) sampler.continentalness().compute(this),
				(float) sampler.erosion().compute(this),
				(float) sampler.depth().compute(this),
				(float) sampler.weirdness().compute(this)
			);
		}

		/**
		 * Gets the {@link Climate.Sampler} for sampling climate parameters.
		 * <p>This sampler is optimized for repeated usage at this context's position.</p>
		 *
		 * @return The {@link Climate.Sampler} instance for sampling climate parameters.
		 */
		public Climate.Sampler getClimateSampler() {
			return this.optimizedSampler;
		}

		@Override
		public int blockX() {
			return this.blockX;
		}

		@Override
		public int blockY() {
			return this.blockY;
		}

		@Override
		public int blockZ() {
			return this.blockZ;
		}
	}

	/**
	 * The interface used for selecting biomes in {@link com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlice} instances.
	 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource
	 */
	public interface ModdedBiomeProvider {
		Codec<ModdedBiomeProvider> CODEC = BiomeUtil.MODDED_PROVIDERS.dispatchStable(ModdedBiomeProvider::codec, Function.identity());
		Set<Holder<Biome>> EMPTY_POSSIBLE_BIOMES = ImmutableSet.of();

		/**
		 * Called just before this provider is ready to be assigned to biome sources.
		 * <p>Use this method to do any initialization that needs server data.</p>
		 *
		 * @param server The server of the world.
		 * @param seed   The seed of the world.
		 */
		default void finalize(MinecraftServer server, long seed) {}

		/**
		 * Gets a holder of a noise {@link Biome} at a position in a modded slice.
		 *
		 * @param x        The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param y        The y pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param z        The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param context  A {@link ScopedDensityFunctionContext} instance for efficient use of common sampling.
		 * @param original The original {@link BiomeSource} instance that this provider is modding.
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return A noise {@link Biome} at a position in a modded slice.
		 */
		default Holder<Biome> getNoiseBiome(int x, int y, int z, ScopedDensityFunctionContext context, BiomeSource original, Registry<Biome> registry) {
			return this.getNoiseBiome(x, y, z, context.getClimateSampler(), original, registry);
		}

		/**
		 * Gets a holder of a noise {@link Biome} at a position in a modded slice.
		 * <p>In newer Minecraft versions, this method will get removed!</p>
		 * <p>Use the fuller form of this method instead.</p>
		 *
		 * @param x        The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param y        The y pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param z        The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param sampler  A {@link Climate.Sampler} instance to sample {@link net.minecraft.world.level.biome.Climate.TargetPoint} instances.
		 * @param original The original {@link BiomeSource} instance that this provider is modding.
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return A noise {@link Biome} at a position in a modded slice.
		 */
		@Deprecated
		Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry);

		/**
		 * Gets the set of the additional possible biomes that this provider may return.
		 * <p>Used by Blueprint to determine if the provider places nothing significant.</p>
		 *
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return The set of the additional possible biomes that this provider may return.
		 */
		Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry);

		/**
		 * Gets the set of the possible biomes that this provider may return.
		 * <p>Used by Blueprint for building the possible biomes list in biome sources.</p>
		 *
		 * @param originalPossibleBiomes The set of original (non-blueprint-modified) possible biomes.
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return The set of the possible biomes that this provider may return.
		 */
		default Set<Holder<Biome>> getPossibleBiomes(Set<Holder<Biome>> originalPossibleBiomes, Registry<Biome> registry) {
			return this.getAdditionalPossibleBiomes(registry);
		}

		/**
		 * Gets a {@link MapCodec} instance for serializing and deserializing this provider.
		 *
		 * @return A {@link MapCodec} instance for serializing and deserializing this provider.
		 */
		MapCodec<? extends ModdedBiomeProvider> codec();
	}

	/**
	 * A simple {@link ModdedBiomeProvider} implementation that uses the original biome source's {@link BiomeSource#getNoiseBiome(int, int, int, Climate.Sampler)} method.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public enum OriginalModdedBiomeProvider implements ModdedBiomeProvider {
		INSTANCE;

		public static final MapCodec<OriginalModdedBiomeProvider> CODEC = MapCodec.unit(INSTANCE);

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, ScopedDensityFunctionContext context, BiomeSource original, Registry<Biome> registry) {
			return context.getOriginalBiome(x, y, z, original);
		}

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			return original.getNoiseBiome(x, y, z, sampler);
		}

		@Override
		public MapCodec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}

		@Override
		public Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry) {
			return EMPTY_POSSIBLE_BIOMES;
		}

		@Override
		public Set<Holder<Biome>> getPossibleBiomes(Set<Holder<Biome>> originalPossibleBiomes, Registry<Biome> registry) {
			return originalPossibleBiomes;
		}
	}

	/**
	 * A {@link ModdedBiomeProvider} implementation that uses a {@link Climate.ParameterList} instance for selecting its biomes and includes handy ways of creating the list.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class MultiNoiseModdedBiomeProvider implements ModdedBiomeProvider {
		private static final MapCodec<MultiNoiseModdedBiomeProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
			return instance.group(
					NullableFieldCodec.nullable("areas", Codec.unboundedMap(BIOME_KEY_CODEC, BIOME_KEY_CODEC), ImmutableMap.of()).forGetter(provider -> provider.areas),
					Codec.either(MultiNoiseBiomeSourceParameterList.CODEC, ExtraCodecs.nonEmptyList(RecordCodecBuilder.<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>create(pairInstance -> pairInstance.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), BIOME_KEY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(pairInstance, Pair::of)).listOf())).fieldOf("biomes").forGetter(provider -> provider.rawBiomes),
					Codec.BOOL.optionalFieldOf("only_map_from_areas", false).forGetter(provider -> provider.onlyMapFromAreas),
					RegistryOps.retrieveGetter(Registries.BIOME)
			).apply(instance, (areas, rawBiomes, onlyMapFromAreas, getter) -> {
				ImmutableList.Builder<Pair<Climate.ParameterPoint, Holder<Biome>>> builder = ImmutableList.builder();
				var biomes = rawBiomes.map(
						preset -> preset.value().parameters().values().stream().map(pair -> Pair.of(pair.getFirst(), pair.getSecond().unwrapKey().orElseThrow())).toList(),
						list -> list
				);
				var originalSourceMarker = getter.getOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER);
				for (var pointWithKey : biomes) {
					var key = pointWithKey.getSecond();
					var keyForKey = areas.get(key);
					builder.add(Pair.of(pointWithKey.getFirst(), keyForKey != null ? getter.getOrThrow(keyForKey) : (onlyMapFromAreas ? originalSourceMarker : getter.get(key).orElse(originalSourceMarker))));
				}
				return new MultiNoiseModdedBiomeProvider(areas, rawBiomes, new Climate.ParameterList<>(builder.build()), onlyMapFromAreas);
			});
		});
		private final Map<ResourceKey<Biome>, ResourceKey<Biome>> areas;
		private final Either<Holder<MultiNoiseBiomeSourceParameterList>, List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>> rawBiomes;
		private final Climate.ParameterList<Holder<Biome>> biomes;
		private final boolean onlyMapFromAreas;

		private MultiNoiseModdedBiomeProvider(Map<ResourceKey<Biome>, ResourceKey<Biome>> areas, Either<Holder<MultiNoiseBiomeSourceParameterList>, List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>> rawBiomes, Climate.ParameterList<Holder<Biome>> biomes, boolean onlyMapFromAreas) {
			this.areas = areas;
			this.rawBiomes = rawBiomes;
			this.biomes = biomes;
			this.onlyMapFromAreas = onlyMapFromAreas;
		}

		/**
		 * Constructs a new {@link Builder} instance.
		 *
		 * @return A new {@link Builder} instance.
		 */
		public static Builder builder() {
			return new Builder();
		}

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, ScopedDensityFunctionContext context, BiomeSource original, Registry<Biome> registry) {
			return this.biomes.findValue(context.getTargetPoint());
		}

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			return this.biomes.findValue(sampler.sample(x, y, z));
		}

		@Override
		public MapCodec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}

		@Override
		public Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry) {
			return this.biomes.values().stream().map(Pair::getSecond).collect(Collectors.toSet());
		}

		/**
		 * The builder class for the {@link BiomeUtil.MultiNoiseModdedBiomeProvider} class.
		 *
		 * @author SmellyModder (Luke Tonon)
		 */
		public static final class Builder {
			private final ImmutableMap.Builder<ResourceKey<Biome>, ResourceKey<Biome>> areas = ImmutableMap.builder();
			private Either<Holder<MultiNoiseBiomeSourceParameterList>, List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>> rawBiomes = Either.right(ImmutableList.of());
			private boolean onlyMapFromAreas = true;

			/**
			 * Tells the provider to map all biome keys to another biome key.
			 * <p>Useful for grouping various parts of your parameter list or for replacing specific biomes in an existing parameter list.</p>
			 *
			 * @param key   The {@link ResourceKey} of {@link Biome} to use for remapping all parameter list entries with the key.
			 * @param value The new {@link ResourceKey} of {@link Biome} to put in place.
			 * @return This builder.
			 */
			public Builder area(ResourceKey<Biome> key, ResourceKey<Biome> value) {
				this.areas.put(key, value);
				return this;
			}

			/**
			 * Tells the provider to have its parameter list come from a {@link MultiNoiseBiomeSourceParameterList} instance.
			 * <p>Useful for using an existing parameter list preset.</p>
			 *
			 * @param preset A {@link Holder} of {@link MultiNoiseBiomeSourceParameterList} to use.
			 * @return This builder.
			 */
			public Builder biomes(Holder<MultiNoiseBiomeSourceParameterList> preset) {
				this.rawBiomes = Either.left(preset);
				return this;
			}

			/**
			 * Tells the provider to have its parameter list contain entries from a consumer.
			 *
			 * @param consumer A consumer to add the entries.
			 * @return This builder.
			 */
			public Builder biomes(Consumer<Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>> consumer) {
				ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> builder = ImmutableList.builder();
				consumer.accept(builder::add);
				this.rawBiomes = Either.right(builder.build());
				return this;
			}

			/**
			 * Tells the provider to never attempt to map parameter list entry keys to the key of a registered biome.
			 *
			 * @param onlyMapFromAreas If the provider should never attempt to map parameter list entry keys to the key of a registered biome.
			 * @return This builder.
			 */
			public Builder onlyMapFromAreas(boolean onlyMapFromAreas) {
				this.onlyMapFromAreas = onlyMapFromAreas;
				return this;
			}

			/**
			 * Builds a {@link MultiNoiseModdedBiomeProvider} instance from this builder.
			 *
			 * @return A new {@link MultiNoiseModdedBiomeProvider} instance from this builder.
			 */
			public MultiNoiseModdedBiomeProvider build() {
				return new MultiNoiseModdedBiomeProvider(this.areas.build(), this.rawBiomes, null, this.onlyMapFromAreas);
			}
		}
	}

	/**
	 * A {@link ModdedBiomeProvider} implementation that maps out {@link ModdedBiomeProvider} instances for overlaying specific biomes.
	 * <p>By default, the {@code underlay} biomes come from {@link OriginalModdedBiomeProvider}.</p>
	 * <p>
	 *     The {@code markUnderlayBiomesAsOriginal} parameter tells
	 *     it to return the original source marker biome when
	 *     no matching overlay fits the sampled {@code underlay} biome.
	 *     By default, it is true.
	 * </p>
	 * <p>This provider is especially useful for sub-biomes and simple replacements.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record OverlayModdedBiomeProvider(List<Pair<HolderSet<Biome>, ModdedBiomeProvider>> overlays, ModdedBiomeProvider underlay, boolean markUnderlayBiomesAsOriginal) implements ModdedBiomeProvider {
		public static final MapCodec<OverlayModdedBiomeProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
			return instance.group(
				Codec.mapPair(RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("matches_biomes"), Codec.mapEither(BiomeSource.CODEC.fieldOf("biome_source"), BlueprintExtraCodecs.lazyMapCodec(() -> ModdedBiomeProvider.CODEC.fieldOf("provider"))).xmap(
					either -> {
						return either.map(BiomeSourceModdedBiomeProvider::new, provider -> provider);
					}, provider -> {
						return provider instanceof BiomeSourceModdedBiomeProvider source ? Either.left(source.biomeSource) : Either.right(provider);
					}
				)).codec().listOf().fieldOf("overlays").forGetter(provider -> provider.overlays),
				BlueprintExtraCodecs.lazyMapCodec(() -> ModdedBiomeProvider.CODEC.optionalFieldOf("underlay", OriginalModdedBiomeProvider.INSTANCE)).forGetter(provider -> provider.underlay),
				Codec.BOOL.optionalFieldOf("mark_underlay_biomes_as_original", true).forGetter(provider -> provider.markUnderlayBiomesAsOriginal)
			).apply(instance, OverlayModdedBiomeProvider::new);
		});

		public OverlayModdedBiomeProvider(List<Pair<HolderSet<Biome>, BiomeSource>> overlays) {
			this(overlays.stream().map(pair -> Pair.of(pair.getFirst(), (ModdedBiomeProvider) new BiomeSourceModdedBiomeProvider(pair.getSecond()))).toList(), OriginalModdedBiomeProvider.INSTANCE, true);
		}

		public static OverlayModdedBiomeProvider overlays(List<Pair<HolderSet<Biome>, ModdedBiomeProvider>> overlays) {
			return new OverlayModdedBiomeProvider(overlays, OriginalModdedBiomeProvider.INSTANCE, true);
		}

		@Override
		public void finalize(MinecraftServer server, long seed) {
			this.overlays.forEach(overlay -> overlay.getSecond().finalize(server, seed));
			this.underlay.finalize(server, seed);
		}

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, ScopedDensityFunctionContext context, BiomeSource original, Registry<Biome> registry) {
			// Duplicated code looks weird here but fixing it
			// without performance loss adds even more complexity
			// Annoyances of not wanting to make breaking changes
			var underlayBiome = this.underlay.getNoiseBiome(x, y, z, context, original, registry);
			for (var overlay : this.overlays) {
				if (overlay.getFirst().contains(underlayBiome))
					return overlay.getSecond().getNoiseBiome(x, y, z, context, original, registry);
			}
			return this.markUnderlayBiomesAsOriginal ? registry.getHolderOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER) : underlayBiome;
		}

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			var underlayBiome = this.underlay.getNoiseBiome(x, y, z, sampler, original, registry);
			for (var overlay : this.overlays) {
				if (overlay.getFirst().contains(underlayBiome))
					return overlay.getSecond().getNoiseBiome(x, y, z, sampler, original, registry);
			}
			return this.markUnderlayBiomesAsOriginal ? registry.getHolderOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER) : underlayBiome;
		}

		@Override
		public Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry) {
			return this.getPossibleBiomes(EMPTY_POSSIBLE_BIOMES, registry);
		}

		@Override
		public Set<Holder<Biome>> getPossibleBiomes(Set<Holder<Biome>> originalPossibleBiomes, Registry<Biome> registry) {
			HashSet<Holder<Biome>> biomes = new HashSet<>();
			if (this.markUnderlayBiomesAsOriginal) {
				this.overlays.forEach(overlay -> biomes.addAll(overlay.getSecond().getPossibleBiomes(originalPossibleBiomes, registry)));
				return biomes;
			}
			Set<Holder<Biome>> underlayBiomes = new HashSet<>(this.underlay.getPossibleBiomes(originalPossibleBiomes, registry));
			for (var overlay : this.overlays) {
				var underlayIterator = underlayBiomes.iterator();
				var overlayBiomes = overlay.getSecond().getPossibleBiomes(originalPossibleBiomes, registry);
				var overlayMatches = overlay.getFirst();
				while (underlayIterator.hasNext()) {
					var underlayBiome = underlayIterator.next();
					if (overlayMatches.contains(underlayBiome)) {
						if (overlayBiomes.contains(underlayBiome)) continue;
						// Overlay removes the biome
						underlayIterator.remove();
					}
				}
				biomes.addAll(overlayBiomes);
			}
			biomes.addAll(underlayBiomes);
			return biomes;
		}

		@Override
		public MapCodec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}
	}

	/**
	 * A {@link ModdedBiomeProvider} implementation that uses a {@link BiomeSource} instance for selecting its biomes.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record BiomeSourceModdedBiomeProvider(BiomeSource biomeSource) implements ModdedBiomeProvider {
		public static final MapCodec<BiomeSourceModdedBiomeProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
			return instance.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter(provider -> provider.biomeSource)
			).apply(instance, BiomeSourceModdedBiomeProvider::new);
		});

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			return this.biomeSource.getNoiseBiome(x, y, z, sampler);
		}

		@Override
		public Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry) {
			return this.biomeSource.possibleBiomes();
		}

		@Override
		public MapCodec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}
	}
}
