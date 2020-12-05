package com.minecraftabnormals.abnormals_core.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

/**
 * This class holds a list of useful methods for generation
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class GenerationUtil {
	@SuppressWarnings("deprecation")
	public static final Predicate<BlockState> IS_AIR = (state) -> {
		return state.isAir();
	};

	public static final Predicate<BlockState> IS_FLUID(int minLevel, Tag<Fluid> allowedFluids) {
		return (state) -> {
			FluidState fluid = state.getFluidState();
			return !fluid.isEmpty() && fluid.getHeight() >= minLevel && fluid.isTagged(allowedFluids);
		};
	}

	public static void fillAreaWithBlockCube(IWorld world, int x1, int y1, int z1, int x2, int y2, int z2, BlockState block, @Nullable Predicate<BlockState> canPlace) {
		BlockPos.Mutable positions = new BlockPos.Mutable();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.setPos(xx, yy, zz);
					if (canPlace == null || canPlace.test(world.getBlockState(positions))) {
						world.setBlockState(positions, block, 2);
					}
				}
			}
		}
	}

	public static void fillAreaWithBlockCube(IWorld world, Random rand, int x1, int y1, int z1, int x2, int y2, int z2, @Nullable Predicate<BlockState> canPlace, BlockPlacementEntry... states) {
		BlockPos.Mutable positions = new BlockPos.Mutable();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.setPos(xx, yy, zz);
					if (canPlace == null || canPlace.test(world.getBlockState(positions))) {
						world.setBlockState(positions, BlockPlacementEntry.getRandomState(rand, Arrays.asList(states)), 2);
					}
				}
			}
		}
	}

	public static void fillAreaWithBlockCubeEdged(IWorld world, int x1, int y1, int z1, int x2, int y2, int z2, BlockState block, @Nullable Predicate<BlockState> canPlace) {
		BlockPos.Mutable positions = new BlockPos.Mutable();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.setPos(xx, yy, zz);
					if ((canPlace == null || canPlace.test(world.getBlockState(positions))) && (xx == x2 || zz == z2)) {
						world.setBlockState(positions, block, 2);
					}
				}
			}
		}
	}

	public static void fillAreaWithBlockCubeEdged(IWorld world, Random rand, int x1, int y1, int z1, int x2, int y2, int z2, @Nullable Predicate<BlockState> canPlace, BlockPlacementEntry... states) {
		BlockPos.Mutable positions = new BlockPos.Mutable();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.setPos(xx, yy, zz);
					if ((canPlace == null || canPlace.test(world.getBlockState(positions))) && (xx == x2 || zz == z2)) {
						world.setBlockState(positions, BlockPlacementEntry.getRandomState(rand, Arrays.asList(states)), 2);
					}
				}
			}
		}
	}

	public static class BlockPlacementEntry {
		private final BlockState state;
		private final int weight;

		public BlockPlacementEntry(BlockState state, int weight) {
			this.state = state;
			this.weight = weight;
		}

		public static BlockState getRandomState(Random rand, List<BlockPlacementEntry> entries) {
			int randTotalWeight = rand.nextInt(getTotalWeight(entries));
			for (int i = 0; i < entries.size(); i++) {
				BlockPlacementEntry entry = entries.get(i);
				randTotalWeight -= entry.weight;
				if (randTotalWeight < 0) {
					return entry.state;
				}
			}
			return null;
		}

		private static int getTotalWeight(List<BlockPlacementEntry> entries) {
			int totalWeight = 0;
			for (BlockPlacementEntry entry : entries) {
				totalWeight += entry.weight;
			}
			return totalWeight;
		}
	}
}