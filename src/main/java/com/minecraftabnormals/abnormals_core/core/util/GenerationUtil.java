package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.SetTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class holds a list of useful methods for generation
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class GenerationUtil {
	public static final Predicate<BlockState> IS_AIR = BlockBehaviour.BlockStateBase::isAir;

	public static Predicate<BlockState> isFluid(int minLevel, SetTag<Fluid> allowedFluids) {
		return (state) -> {
			FluidState fluid = state.getFluidState();
			return !fluid.isEmpty() && fluid.getOwnHeight() >= minLevel && fluid.is(allowedFluids);
		};
	}

	public static void fillAreaWithBlockCube(LevelAccessor level, int x1, int y1, int z1, int x2, int y2, int z2, BlockState block, @Nullable Predicate<BlockState> canPlace) {
		BlockPos.MutableBlockPos positions = new BlockPos.MutableBlockPos();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.set(xx, yy, zz);
					if (canPlace == null || canPlace.test(level.getBlockState(positions))) {
						level.setBlock(positions, block, 2);
					}
				}
			}
		}
	}

	public static void fillAreaWithBlockCube(LevelAccessor level, Random rand, int x1, int y1, int z1, int x2, int y2, int z2, @Nullable Predicate<BlockState> canPlace, WeightedRandomList<BlockPlacementEntry> states) {
		BlockPos.MutableBlockPos positions = new BlockPos.MutableBlockPos();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.set(xx, yy, zz);
					if (canPlace == null || canPlace.test(level.getBlockState(positions))) {
						level.setBlock(positions, states.getRandom(rand).get().getState(), 2);
					}
				}
			}
		}
	}

	public static void fillAreaWithBlockCubeEdged(LevelAccessor level, int x1, int y1, int z1, int x2, int y2, int z2, BlockState block, @Nullable Predicate<BlockState> canPlace) {
		BlockPos.MutableBlockPos positions = new BlockPos.MutableBlockPos();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.set(xx, yy, zz);
					if ((canPlace == null || canPlace.test(level.getBlockState(positions))) && (xx == x2 || zz == z2)) {
						level.setBlock(positions, block, 2);
					}
				}
			}
		}
	}

	public static void fillAreaWithBlockCubeEdged(LevelAccessor level, Random rand, int x1, int y1, int z1, int x2, int y2, int z2, @Nullable Predicate<BlockState> canPlace, WeightedRandomList<BlockPlacementEntry> states) {
		BlockPos.MutableBlockPos positions = new BlockPos.MutableBlockPos();
		for (int xx = x1; xx <= x2; xx++) {
			for (int yy = y1; yy <= y2; yy++) {
				for (int zz = z1; zz <= z2; zz++) {
					positions.set(xx, yy, zz);
					if ((canPlace == null || canPlace.test(level.getBlockState(positions))) && (xx == x2 || zz == z2)) {
						level.setBlock(positions, states.getRandom(rand).get().getState(), 2);
					}
				}
			}
		}
	}

	public static class BlockPlacementEntry implements WeightedEntry {
		private final BlockState state;
		private final Weight weight;

		public BlockPlacementEntry(BlockState state, int weight) {
			this.state = state;
			this.weight = Weight.of(weight);
		}

		public BlockState getState() {
			return this.state;
		}

		@Override
		public Weight getWeight() {
			return this.weight;
		}
	}
}