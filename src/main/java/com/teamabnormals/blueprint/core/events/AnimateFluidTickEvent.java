package com.teamabnormals.blueprint.core.events;

import com.teamabnormals.blueprint.core.mixin.client.ClientLevelMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired before {@link net.minecraft.world.level.material.FluidState#animateTick(Level, BlockPos, RandomSource)} gets called in {@link net.minecraft.client.multiplayer.ClientLevel}.
 * <p>Canceling this event will prevent the original method from being called.</p>
 *
 * @author abigailfails
 * @see ClientLevelMixin
 */
@Cancelable
public final class AnimateFluidTickEvent extends Event {
	private final Level level;
	private final BlockPos pos;
	private final FluidState state;
	private final RandomSource random;

	public AnimateFluidTickEvent(Level level, BlockPos pos, FluidState state, RandomSource random) {
		this.level = level;
		this.pos = pos;
		this.state = state;
		this.random = random;
	}

	/**
	 * Fires the {@link AnimateFluidTickEvent} for a given {@link FluidState}, {@link Level}, {@link BlockPos} and {@link RandomSource}.
	 *
	 * @param level The {@link Level} that the {@code state} is in.
	 * @param pos   The {@link BlockPos} that the {@code state} is at.
	 * @param state The {@link FluidState} that {@link net.minecraft.world.level.material.FluidState#animateTick(Level, BlockPos, RandomSource)} is being fired for.
	 * @param rand  The {@link RandomSource} to be used for randomizing particle placement.
	 */
	public static boolean onAnimateFluidTick(Level level, BlockPos pos, FluidState state, RandomSource rand) {
		return MinecraftForge.EVENT_BUS.post(new AnimateFluidTickEvent(level, pos, state, rand));
	}

	/**
	 * Gets the {@link #level} this event was fired from.
	 *
	 * @return The {@link #level} this event was fired from.
	 */
	public Level getLevel() {
		return this.level;
	}

	/**
	 * Gets the {@link #pos} of where this event was fired at.
	 *
	 * @return The {@link #pos} of where this event was fired at.
	 */
	public BlockPos getPos() {
		return this.pos;
	}

	/**
	 * Gets the {@link #state} the event was fired for.
	 *
	 * @return The {@link #state} the event was fired for.
	 */
	public FluidState getState() {
		return this.state;
	}

	/**
	 * Gets the {@link #random} used for animate ticking the fluid.
	 *
	 * @return The {@link #random} used for animate ticking the fluid.
	 */
	public RandomSource getRandom() {
		return this.random;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
