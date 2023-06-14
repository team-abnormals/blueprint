package com.teamabnormals.blueprint.core.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.NullableFieldCodec;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private static final BasicRegistry<Codec<? extends ModdedBiomeProvider>> MODDED_PROVIDERS = new BasicRegistry<>();
	public static final Codec<ResourceKey<Biome>> BIOME_KEY_CODEC = ResourceKey.codec(Registries.BIOME);

	static {
		MODDED_PROVIDERS.register(new ResourceLocation(Blueprint.MOD_ID, "original"), BiomeUtil.OriginalModdedBiomeProvider.CODEC);
		MODDED_PROVIDERS.register(new ResourceLocation(Blueprint.MOD_ID, "multi_noise"), BiomeUtil.MultiNoiseModdedBiomeProvider.CODEC);
		MODDED_PROVIDERS.register(new ResourceLocation(Blueprint.MOD_ID, "overlay"), BiomeUtil.OverlayModdedBiomeProvider.CODEC);
		MODDED_PROVIDERS.register(new ResourceLocation(Blueprint.MOD_ID, "biome_source"), BiomeUtil.BiomeSourceModdedBiomeProvider.CODEC);
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
	 * The interface used for selecting biomes in {@link com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlice} instances.
	 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource
	 */
	public interface ModdedBiomeProvider {
		Codec<ModdedBiomeProvider> CODEC = BiomeUtil.MODDED_PROVIDERS.dispatchStable(ModdedBiomeProvider::codec, Function.identity());

		/**
		 * Gets a holder of a noise {@link Biome} at a position in a modded slice.
		 *
		 * @param x        The x pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param y        The y pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param z        The z pos, shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
		 * @param sampler  A {@link Climate.Sampler} instance to sample {@link net.minecraft.world.level.biome.Climate.TargetPoint} instances.
		 * @param original The original {@link BiomeSource} instance that this provider is modding.
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return A noise {@link Biome} at a position in a modded slice.
		 */
		Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry);

		/**
		 * Gets a set of the additional possible biomes that this provider may have.
		 *
		 * @param registry The biome {@link Registry} instance to use if needed.
		 * @return A set of the additional possible biomes that this provider may have.
		 */
		Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry);

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
	public enum OriginalModdedBiomeProvider implements ModdedBiomeProvider {
		INSTANCE;

		public static final Codec<OriginalModdedBiomeProvider> CODEC = Codec.unit(INSTANCE);
		private static final Set<Holder<Biome>> POSSIBLE_BIOMES = ImmutableSet.of();

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			return original.getNoiseBiome(x, y, z, sampler);
		}

		@Override
		public Codec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}

		@Override
		public Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry) {
			return POSSIBLE_BIOMES;
		}
	}

	/**
	 * A {@link ModdedBiomeProvider} implementation that uses a {@link Climate.ParameterList} instance for selecting its biomes and includes handy ways of creating the list.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class MultiNoiseModdedBiomeProvider implements ModdedBiomeProvider {
		private static final Codec<MultiNoiseModdedBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> {
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
		public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			return this.biomes.findValue(sampler.sample(x, y, z));
		}

		@Override
		public Codec<? extends ModdedBiomeProvider> codec() {
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
	 * A {@link ModdedBiomeProvider} implementation that maps out {@link BiomeSource} instances for overlaying specific biomes.
	 * <p>This is especially useful for sub-biomes.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record OverlayModdedBiomeProvider(List<Pair<HolderSet<Biome>, BiomeSource>> overlays) implements ModdedBiomeProvider {
		public static final Codec<OverlayModdedBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					Codec.mapPair(RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("matches_biomes"), BiomeSource.CODEC.fieldOf("biome_source")).codec().listOf().fieldOf("overlays").forGetter(provider -> provider.overlays)
			).apply(instance, OverlayModdedBiomeProvider::new);
		});

		@Override
		public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, BiomeSource original, Registry<Biome> registry) {
			Holder<Biome> originalBiome = original.getNoiseBiome(x, y, z, sampler);
			for (var overlay : this.overlays) {
				if (overlay.getFirst().contains(originalBiome)) return overlay.getSecond().getNoiseBiome(x, y, z, sampler);
			}
			return registry.getHolderOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER);
		}

		@Override
		public Set<Holder<Biome>> getAdditionalPossibleBiomes(Registry<Biome> registry) {
			HashSet<Holder<Biome>> biomes = new HashSet<>();
			this.overlays.forEach(overlay -> biomes.addAll(overlay.getSecond().possibleBiomes()));
			return biomes;
		}

		@Override
		public Codec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}
	}

	/**
	 * A {@link ModdedBiomeProvider} implementation that uses a {@link BiomeSource} instance for selecting its biomes.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record BiomeSourceModdedBiomeProvider(BiomeSource biomeSource) implements ModdedBiomeProvider {
		public static final Codec<BiomeSourceModdedBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> {
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
		public Codec<? extends ModdedBiomeProvider> codec() {
			return CODEC;
		}
	}
}
