package com.teamabnormals.blueprint.core.events;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * This class holds two events related to the ticking client-side effects for blocks and fluids.
 * <p>The first event {@link #BLOCK} is for injecting into {@link net.minecraft.world.level.block.Block#animateTick(BlockState, Level, BlockPos, RandomSource)}.</p>
 * <p>The second event {@link #FLUID} is for injecting into {@link net.minecraft.world.level.material.Fluid#animateTick(Level, BlockPos, FluidState, RandomSource)}.</p>
 *
 * @author abigailfails
 * @author SmellyModder (Luke Tonon)
 */
public interface AnimateTickEvents {
	SimpleEvent<AnimateTickEvents.Block> BLOCK = new SimpleEvent<>(AnimateTickEvents.Block.class, (listeners) -> (state, level, pos, randomSource) -> {
		for (AnimateTickEvents.Block listener : listeners) {
			if (!listener.animateTick(state, level, pos, randomSource)) {
				return false;
			}
		}
		return true;
	});

	SimpleEvent<AnimateTickEvents.Fluid> FLUID = new SimpleEvent<>(AnimateTickEvents.Fluid.class, (listeners) -> (state, level, pos, randomSource) -> {
		for (AnimateTickEvents.Fluid listener : listeners) {
			if (!listener.animateFluidTick(state, level, pos, randomSource)) {
				return false;
			}
		}
		return true;
	});

	/**
	 * Handles the processing of the {@link #BLOCK} event.
	 * <p><b>This method is for internal use only!</b></p>
	 */
	@Deprecated
	static boolean onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
		return BLOCK.getInvoker().animateTick(state, level, pos, randomSource);
	}

	/**
	 * Handles the processing of the {@link #FLUID} event.
	 * <p><b>This method is for internal use only!</b></p>
	 */
	@Deprecated
	static boolean onAnimateFluidTick(FluidState state, Level level, BlockPos pos, RandomSource rand) {
		return FLUID.getInvoker().animateFluidTick(state, level, pos, rand);
	}

	/**
	 * The functional interface for representing listeners of the {@link #BLOCK} event.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	@FunctionalInterface
	interface Block {
		/**
		 * Called when this listener is fired from {@link net.minecraft.world.level.block.Block#animateTick(BlockState, Level, BlockPos, RandomSource)}.
		 *
		 * @param state        The {@link BlockState} instance at the position in the level.
		 * @param level        The {@link Level} instance where the ticking is occurring.
		 * @param pos          The block position of where the ticking is occurring.
		 * @param randomSource A {@link RandomSource} instance for generating random numbers.
		 * @return If the event should not get cancelled.
		 */
		boolean animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource);
	}

	/**
	 * The functional interface for representing listeners of the {@link #FLUID} event.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	@FunctionalInterface
	interface Fluid {
		/**
		 * Called when this listener is fired from {@link net.minecraft.world.level.material.Fluid#animateTick(BlockState, Level, BlockPos, RandomSource)}.
		 *
		 * @param state        The {@link FluidState} instance at the position in the level.
		 * @param level        The {@link Level} instance where the ticking is occurring.
		 * @param pos          The block position of where the ticking is occurring.
		 * @param randomSource A {@link RandomSource} instance for generating random numbers.
		 * @return If the event should not get cancelled.
		 */
		boolean animateFluidTick(FluidState state, Level level, BlockPos pos, RandomSource randomSource);
	}
}
