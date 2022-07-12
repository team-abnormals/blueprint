package com.teamabnormals.blueprint.core.events;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired before {@link net.minecraft.world.level.block.Block#animateTick Block.animateTick()}.
 * <p>Canceling this event will prevent the original method from being called.</p>
 *
 * @author abigailfails
 */
@Cancelable
public final class AnimateTickEvent extends BlockEvent {
	private final RandomSource random;

	public AnimateTickEvent(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		super(level, pos, state);
		this.random = rand;
	}

	/**
	 * Fires the {@link AnimateTickEvent} for a given {@link BlockState}, {@link Level}, {@link BlockPos} and {@link RandomSource}.
	 *
	 * @param state The {@link BlockState} that {@link net.minecraft.world.level.block.Block#animateTick Block.animateTick()} is being fired for.
	 * @param level The {@link Level} that the {@code state} is in.
	 * @param pos   The {@link BlockPos} that the {@code state} is at.
	 * @param randomSource The {@link RandomSource} to be used for randomizing particle placement.
	 */
	public static boolean onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
		return MinecraftForge.EVENT_BUS.post(new AnimateTickEvent(state, level, pos, randomSource));
	}

	/**
	 * Gets the {@link #random} used for ticking the state.
	 *
	 * @return The {@link #random} used for ticking the state.
	 */
	public RandomSource getRandom() {
		return this.random;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
