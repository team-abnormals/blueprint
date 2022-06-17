package com.teamabnormals.blueprint.common.world.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	private final ThreadLocal<SlicesCache> slicesCache = ThreadLocal.withInitial(SlicesCache::new);
	private final ModdedBiomeSlice[] slices;
	private final int totalWeight;
	private final int size;
	private final Biome originalSourceMarker;
	private final long slicesSeed;
	private final long slicesZoomSeed;
	private final long obfuscatedSeed;

	public ModdedBiomeSource(Registry<Biome> biomes, BiomeSource originalSource, ArrayList<ModdedBiomeSlice> slices, int size, long seed, long dimensionSeedModifier) {
		this(biomes, originalSource, slices, size + Mth.ceil(Math.log(slices.size()) / Math.log(2)), seed, seed + 1791510900 + dimensionSeedModifier, seed - 771160217 + dimensionSeedModifier);
	}

	public ModdedBiomeSource(Registry<Biome> biomes, BiomeSource originalSource, ArrayList<ModdedBiomeSlice> slices, int size, long seed, long slicesSeed, long slicesZoomSeed) {
		super(new ArrayList<>(combinePossibleBiomes(originalSource.possibleBiomes(), slices, biomes)));
		this.biomes = biomes;
		this.originalSource = originalSource;
		this.slices = slices.toArray(new ModdedBiomeSlice[0]);
		this.totalWeight = Stream.of(this.slices).map(ModdedBiomeSlice::weight).reduce(0, Integer::sum);
		this.size = size;
		this.originalSourceMarker = biomes.getOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER.getKey());
		this.slicesSeed = slicesSeed;
		this.slicesZoomSeed = slicesZoomSeed;
		this.obfuscatedSeed = BiomeManager.obfuscateSeed(seed);
	}

	private static Set<Holder<Biome>> combinePossibleBiomes(Set<Holder<Biome>> possibleBiomes, ArrayList<ModdedBiomeSlice> slices, Registry<Biome> registry) {
		Set<Holder<Biome>> biomes = new HashSet<>(possibleBiomes);
		for (ModdedBiomeSlice slice : slices) {
			biomes.addAll(slice.provider().getAdditionalPossibleBiomes(registry));
		}
		return biomes;
	}

	@Override
	public void addDebugInfo(List<String> strings, BlockPos pos, Climate.Sampler sampler) {
		BiomeSource original = this.originalSource;
		original.addDebugInfo(strings, pos, sampler);
		if (!(original instanceof ModdedBiomeSource))
			strings.add("Modded Biome Slice: " + this.getSlice(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getZ())).name());
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new ModdedBiomeSource(this.biomes, this.originalSource, new ArrayList<>(List.of(this.slices)), this.size, seed, this.slicesSeed, this.slicesZoomSeed);
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
		Holder<Biome> biome = this.getSlice(x, z).provider().getNoiseBiome(x, y, z, sampler, this.originalSource, this.biomes);
		return biome.value() == this.originalSourceMarker ? this.originalSource.getNoiseBiome(x, y, z, sampler) : biome;
	}

	private ModdedBiomeSlice getSlice(int x, int z) {
		return this.slicesCache.get().getSlice(this, x, z);
	}

	private ModdedBiomeSlice getSliceUncached(int x, int z) {
		int cordX = QuartPos.toBlock(x);
		int cordZ = QuartPos.toBlock(z);
		long slicesZoomSeed = this.slicesZoomSeed;
		//Randomly zooms the x and z coordinates by cutting them into cells and adding some randomness for each zoom
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
		//After transforming the x and z coordinates to be in a randomized cell, we generate the pseudorandom weight associated with the transformed coordinates
		int randomWeight = nextInt(this.slicesSeed, cordX, cordZ, this.totalWeight);
		for (ModdedBiomeSlice slice : this.slices) {
			if ((randomWeight -= slice.weight()) < 0) return slice;
		}
		return this.slices[0];
	}

	/**
	 * Gets the {@link ModdedBiomeSlice} instance at given x, y, and z coordinates after it has been zoomed by vanilla's {@link BiomeManager}.
	 * <p>This method is used internally by {@link com.teamabnormals.blueprint.core.registry.BlueprintSurfaceRules.ModdednessSliceConditionSource}.</p>
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The {@link ModdedBiomeSlice} instance at given x, y, and z coordinates after it has been zoomed by vanilla's {@link BiomeManager}.
	 */
	//Vanilla applies a zoom when getting noise biomes, and we must account for this in ModdednessSliceConditionSource
	public ModdedBiomeSlice getSliceWithVanillaZoom(int x, int y, int z) {
		int i = x - 2;
		int j = y - 2;
		int k = z - 2;
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

		int l2 = (k1 & 4) == 0 ? l : l + 1;
		int j3 = (k1 & 1) == 0 ? j1 : j1 + 1;
		return this.getSlice(l2, j3);
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

	private static int nextInt(long seed, int x, int z, int bound) {
		long next = LinearCongruentialGenerator.next(seed, x);
		next = LinearCongruentialGenerator.next(next, z);
		next = LinearCongruentialGenerator.next(next, x);
		return Math.floorMod(LinearCongruentialGenerator.next(next, z) >> 24, bound);
	}

	//The y-axis doesn't matter for selecting slices, so we can cache our slices on the xz plane to greatly boost performance.
	private static class SlicesCache {
		private final long[] lastXZHashes;
		private final ModdedBiomeSlice[] slices;

		private SlicesCache() {
			Arrays.fill(this.lastXZHashes = new long[256], -9223372036854775807L);
			this.slices = new ModdedBiomeSlice[256];
		}

		private ModdedBiomeSlice getSlice(ModdedBiomeSource biomeSource, int x, int z) {
			int xIndex = SectionPos.sectionRelative(x);
			int zIndex = SectionPos.sectionRelative(z);
			int index = 16 * xIndex + zIndex;
			long xzHash = ChunkPos.asLong(x, z);
			if (this.lastXZHashes[index] != xzHash) {
				this.lastXZHashes[index] = xzHash;
				return this.slices[index] = biomeSource.getSliceUncached(x, z);
			}
			return this.slices[index];
		}
	}
}
