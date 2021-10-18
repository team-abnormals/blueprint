package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.SetTag;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class holds a list of useful stuff for generation.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class GenerationUtil {
	public static final Predicate<BlockState> IS_AIR = BlockBehaviour.BlockStateBase::isAir;

	/**
	 * Gets a predicate to check if a {@link BlockState} is an allowed fluid.
	 *
	 * @param minLevel      The minimum level the fluid be at.
	 * @param allowedFluids A tag to use for the valid fluids.
	 * @return A predicate to check if a {@link BlockState} is an allowed fluid.
	 */
	public static Predicate<BlockState> isFluid(int minLevel, SetTag<Fluid> allowedFluids) {
		return (state) -> {
			FluidState fluid = state.getFluidState();
			return !fluid.isEmpty() && fluid.getOwnHeight() >= minLevel && fluid.is(allowedFluids);
		};
	}

	/**
	 * Fills a specified area using a given {@link LevelAccessor} with a given {@link BlockState} if a given {@link Predicate} is met.
	 *
	 * @param level    A {@link LevelAccessor} to use.
	 * @param x1       Minimum x.
	 * @param y1       Minimum y.
	 * @param z1       Minimum z.
	 * @param x2       Maximum x.
	 * @param y2       Maximum y.
	 * @param z2       Maximum z.
	 * @param block    A {@link BlockState} to fill the area with.
	 * @param canPlace If the {@link BlockState} can replace a found {@link BlockState}.
	 */
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

	/**
	 * Fills a specified area using a given {@link LevelAccessor} with random weighted states if a given {@link Predicate} is met.
	 *
	 * @param level    A {@link LevelAccessor} to use.
	 * @param rand     A {@link Random} to use for randomizing the states.
	 * @param x1       Minimum x.
	 * @param y1       Minimum y.
	 * @param z1       Minimum z.
	 * @param x2       Maximum x.
	 * @param y2       Maximum y.
	 * @param z2       Maximum z.
	 * @param canPlace If the {@link BlockState} can replace a found {@link BlockState}.
	 * @param states   A {@link WeightedRandomList} to use for selecting a random {@link BlockState}.
	 */
	public static void fillAreaWithBlockCube(LevelAccessor level, Random rand, int x1, int y1, int z1, int x2, int y2, int z2, @Nullable Predicate<BlockState> canPlace, WeightedRandomList<WeightedStateEntry> states) {
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

	/**
	 * Outlines a specified area using a given {@link LevelAccessor} with a given {@link BlockState} if a given {@link Predicate} is met.
	 *
	 * @param level    A {@link LevelAccessor} to use.
	 * @param x1       Minimum x.
	 * @param y1       Minimum y.
	 * @param z1       Minimum z.
	 * @param x2       Maximum x.
	 * @param y2       Maximum y.
	 * @param z2       Maximum z.
	 * @param block    A {@link BlockState} to fill the area with.
	 * @param canPlace If the {@link BlockState} can replace a found {@link BlockState}.
	 */
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

	/**
	 * Outlines a specified area using a given {@link LevelAccessor} with random weighted states if a given {@link Predicate} is met.
	 *
	 * @param level    A {@link LevelAccessor} to use.
	 * @param rand     A {@link Random} to use for randomizing the states.
	 * @param x1       Minimum x.
	 * @param y1       Minimum y.
	 * @param z1       Minimum z.
	 * @param x2       Maximum x.
	 * @param y2       Maximum y.
	 * @param z2       Maximum z.
	 * @param canPlace If the {@link BlockState} can replace a found {@link BlockState}.
	 * @param states   A {@link WeightedRandomList} to use for selecting a random {@link BlockState}.
	 */
	public static void fillAreaWithBlockCubeEdged(LevelAccessor level, Random rand, int x1, int y1, int z1, int x2, int y2, int z2, @Nullable Predicate<BlockState> canPlace, WeightedRandomList<WeightedStateEntry> states) {
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

	/**
	 * A {@link WeightedEntry} implementation for storing weighted {@link BlockState}s.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class WeightedStateEntry implements WeightedEntry {
		private final BlockState state;
		private final Weight weight;

		public WeightedStateEntry(BlockState state, int weight) {
			this.state = state;
			this.weight = Weight.of(weight);
		}

		/**
		 * Gets this entry's {@link #state}.
		 *
		 * @return This entry's {@link #state}.
		 */
		public BlockState getState() {
			return this.state;
		}

		@Override
		public Weight getWeight() {
			return this.weight;
		}
	}
}