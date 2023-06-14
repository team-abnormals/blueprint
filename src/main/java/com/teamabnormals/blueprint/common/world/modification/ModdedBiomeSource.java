package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A {@link BiomeSource} subclass that wraps another {@link BiomeSource} instance and overlays its biomes with sliced modded biome providers.
 *
 * @author SmellyModder (Luke Tonon)
 * @see ModdedBiomeSlice
 */
public final class ModdedBiomeSource extends BiomeSource {
	public static final Codec<BiomeSource> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				BiomeSource.CODEC.fieldOf("original_biome_source").forGetter(thisBiomeSource -> thisBiomeSource instanceof ModdedBiomeSource moddedBiomeSource ? moddedBiomeSource.originalSource : thisBiomeSource)
		).apply(instance, biomeSource -> biomeSource);
	});
	private final Registry<Biome> biomes;
	private final BiomeSource originalSource;
	private final ThreadLocal<PositionalRandomCache> positionalRandomCache = ThreadLocal.withInitial(PositionalRandomCache::new);
	private final Pair<ResourceLocation, ModdedBiomeSlice>[] slices;
	private final int totalWeight;
	private final int size;
	private final Biome originalSourceMarker;
	private final long slicesSeed;
	private final long slicesZoomSeed;
	private final long obfuscatedSeed;

	@SuppressWarnings("unchecked")
	public ModdedBiomeSource(Registry<Biome> biomes, BiomeSource originalSource, ArrayList<Pair<ResourceLocation, ModdedBiomeSlice>> slices, int size, long seed, long dimensionSeedModifier) {
		this.biomes = biomes;
		this.originalSource = originalSource;
		this.slices = slices.toArray(new Pair[0]);
		this.totalWeight = Stream.of(this.slices).map(pair -> pair.getSecond().weight()).reduce(0, Integer::sum);
		this.size = size;
		this.originalSourceMarker = biomes.getOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER);
		this.slicesSeed = seed + 1791510900 + dimensionSeedModifier;
		this.slicesZoomSeed = seed - 771160217 + dimensionSeedModifier;
		this.obfuscatedSeed = BiomeManager.obfuscateSeed(seed);
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return Stream.concat(this.originalSource.possibleBiomes().stream(), Arrays.stream(this.slices).flatMap(slice -> slice.getSecond().provider().getAdditionalPossibleBiomes(this.biomes).stream()));
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
		return this.getSlice(x, y, z, sampler, true);
	}

	@Override
	public void addDebugInfo(List<String> strings, BlockPos pos, Climate.Sampler sampler) {
		BiomeSource original = this.originalSource;
		original.addDebugInfo(strings, pos, sampler);
		if (!(original instanceof ModdedBiomeSource))
			strings.add("Modded Biome Slice: " + this.getSliceNameVanillaZoom(pos, sampler));
	}

	@SuppressWarnings("unchecked")
	private <T> T getSlice(int x, int y, int z, Climate.Sampler sampler, boolean returnBiome) {
		Pair<ResourceLocation, ModdedBiomeSlice>[] possibleSlices = new Pair[this.slices.length];
		System.arraycopy(this.slices, 0, possibleSlices, 0, this.slices.length);
		int totalWeight = this.totalWeight;
		long random = this.getPositionalRandom(x, z);
		int randomWeight = Math.floorMod(random, totalWeight);
		for (int i = 0; i < possibleSlices.length; ) {
			Pair<ResourceLocation, ModdedBiomeSlice> slice = possibleSlices[i];
			if (slice != null) {
				ModdedBiomeSlice moddedBiomeSlice = slice.getSecond();
				if ((randomWeight -= moddedBiomeSlice.weight()) < 0) {
					Holder<Biome> biome = moddedBiomeSlice.provider().getNoiseBiome(x, y, z, sampler, this.originalSource, this.biomes);
					if (biome.value() == this.originalSourceMarker) {
						possibleSlices[i] = null;
						randomWeight = Math.floorMod(random, totalWeight -= moddedBiomeSlice.weight());
						i = 0;
						continue;
					} else {
						return (T) (returnBiome ? biome : slice.getFirst());
					}
				}
			}
			i++;
		}
		// Should not happen, but could
		return returnBiome ? (T) this.originalSource.getNoiseBiome(x, y, z, sampler) : null;
	}

	private long getPositionalRandom(int x, int z) {
		return this.positionalRandomCache.get().getRandom(this, x, z);
	}

	private long computeZoomedPositionalRandom(int x, int z) {
		int cordX = QuartPos.toBlock(x);
		int cordZ = QuartPos.toBlock(z);
		long slicesZoomSeed = this.slicesZoomSeed;
		// Randomly zooms the x and z coordinates by cutting them into cells and adding some randomness for each zoom
		for (int i = 0; i < this.size; i++) {
			int cellPosX = cordX & 1;
			int cellPosZ = cordZ & 1;
			int cellX = cordX >> 1;
			int cellZ = cordZ >> 1;
			if (cellPosX == 0 && cellPosZ == 0) {
				cordX = cellX;
				cordZ = cellZ;
			} else if (cellPosX == 0) {
				if (nextInt(slicesZoomSeed, cellX << 1, cellZ << 1, 2) == 0) {
					cordZ = cellZ;
				} else {
					cordZ = (cordZ + 1) >> 1;
				}
				cordX = cellX;
			} else if (cellPosZ == 0) {
				if (nextInt(slicesZoomSeed, cellX << 1, cellZ << 1, 2) == 0) {
					cordX = cellX;
				} else {
					cordX = (cordX + 1) >> 1;
				}
				cordZ = cellZ;
			} else {
				int offsetChoice = nextInt(slicesZoomSeed, cellX << 1, cellZ << 1, 4);
				if (offsetChoice == 0) {
					cordX = cellX;
					cordZ = cellZ;
				} else if (offsetChoice == 1) {
					cordX = (cordX + 1) >> 1;
					cordZ = cellZ;
				} else if (offsetChoice == 2) {
					cordX = cellX;
					cordZ = (cordZ + 1) >> 1;
				} else {
					cordX = (cordX + 1) >> 1;
					cordZ = (cordZ + 1) >> 1;
				}
			}
		}
		return next(this.slicesSeed, cordX, cordZ);
	}

	private static int nextInt(long seed, int x, int z, int bound) {
		return Math.floorMod(next(seed, x, z), bound);
	}

	private static long next(long seed, int x, int z) {
		long next = LinearCongruentialGenerator.next(seed, x);
		next = LinearCongruentialGenerator.next(next, z);
		next = LinearCongruentialGenerator.next(next, x);
		return LinearCongruentialGenerator.next(next, z) >> 24;
	}

	@Nullable
	private ResourceLocation getSliceNameVanillaZoom(BlockPos pos, Climate.Sampler sampler) {
		int i = pos.getX() - 2;
		int j = pos.getY() - 2;
		int k = pos.getZ() - 2;
		int l = i >> 2;
		int i1 = j >> 2;
		int j1 = k >> 2;
		double d0 = (double) (i & 3) / 4.0D;
		double d1 = (double) (j & 3) / 4.0D;
		double d2 = (double) (k & 3) / 4.0D;
		int k1 = 0;
		double d3 = Double.POSITIVE_INFINITY;

		for (int l1 = 0; l1 < 8; ++l1) {
			boolean flag = (l1 & 4) == 0;
			boolean flag1 = (l1 & 2) == 0;
			boolean flag2 = (l1 & 1) == 0;
			int i2 = flag ? l : l + 1;
			int j2 = flag1 ? i1 : i1 + 1;
			int k2 = flag2 ? j1 : j1 + 1;
			double d4 = flag ? d0 : d0 - 1.0D;
			double d5 = flag1 ? d1 : d1 - 1.0D;
			double d6 = flag2 ? d2 : d2 - 1.0D;
			double d7 = getFiddledDistance(this.obfuscatedSeed, i2, j2, k2, d4, d5, d6);
			if (d3 > d7) {
				k1 = l1;
				d3 = d7;
			}
		}

		int x = (k1 & 4) == 0 ? l : l + 1;
		int y = (k1 & 2) == 0 ? i1 : i1 + 1;
		int z = (k1 & 1) == 0 ? j1 : j1 + 1;
		return this.getSlice(x, y, z, sampler, false);
	}

	private static double getFiddledDistance(long p_186680_, int p_186681_, int p_186682_, int p_186683_, double p_186684_, double p_186685_, double p_186686_) {
		long $$7 = LinearCongruentialGenerator.next(p_186680_, p_186681_);
		$$7 = LinearCongruentialGenerator.next($$7, p_186682_);
		$$7 = LinearCongruentialGenerator.next($$7, p_186683_);
		$$7 = LinearCongruentialGenerator.next($$7, p_186681_);
		$$7 = LinearCongruentialGenerator.next($$7, p_186682_);
		$$7 = LinearCongruentialGenerator.next($$7, p_186683_);
		double d0 = getFiddle($$7);
		$$7 = LinearCongruentialGenerator.next($$7, p_186680_);
		double d1 = getFiddle($$7);
		$$7 = LinearCongruentialGenerator.next($$7, p_186680_);
		double d2 = getFiddle($$7);
		return Mth.square(p_186686_ + d2) + Mth.square(p_186685_ + d1) + Mth.square(p_186684_ + d0);
	}

	private static double getFiddle(long p_186690_) {
		double d0 = (double) Math.floorMod(p_186690_ >> 24, 1024) / 1024.0D;
		return (d0 - 0.5D) * 0.9D;
	}

	private static class PositionalRandomCache {
		private final long[] lastXZHashes;
		private final long[] randoms;

		private PositionalRandomCache() {
			Arrays.fill(this.lastXZHashes = new long[256], -9223372036854775807L);
			this.randoms = new long[256];
		}

		private long getRandom(ModdedBiomeSource biomeSource, int x, int z) {
			int xIndex = SectionPos.sectionRelative(x);
			int zIndex = SectionPos.sectionRelative(z);
			int index = 16 * xIndex + zIndex;
			long xzHash = ChunkPos.asLong(x, z);
			if (this.lastXZHashes[index] != xzHash) {
				this.lastXZHashes[index] = xzHash;
				return this.randoms[index] = biomeSource.computeZoomedPositionalRandom(x, z);
			}
			return this.randoms[index];
		}
	}
}
