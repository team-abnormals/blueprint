package com.minecraftabnormals.abnormals_core.core.events;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * This event is fired before {@link net.minecraft.world.level.material.Fluid#animateTick  Fluid.animateTick()}.
 * <p>Canceling this event will prevent the original method from being called.</p>
 *
 * @author abigailfails
 */
@Cancelable
public final class AnimateFluidTickEvent extends Event {
    private final Level world;
    private final BlockPos pos;
    private final FluidState state;
    private final Random random;
    
    public AnimateFluidTickEvent(Level world, BlockPos pos, FluidState state, Random random) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.random = random;
    }

    public Level getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public FluidState getState() {
        return this.state;
    }

    public Random getRandom() {
        return this.random;
    }

    /**
     * Fires the {@link AnimateFluidTickEvent} for a given {@link FluidState}, {@link Level}, {@link BlockPos} and {@link Random}.
     * @param world The {@link Level} that the {@code state} is in.
     * @param pos The {@link BlockPos} that the {@code state} is at.
     * @param state  The {@link FluidState} that {@link net.minecraft.world.level.material.Fluid#animateTick Fluid.animateTick()} is being fired for.
     * @param rand The {@link Random} to be used for randomizing particle placement.
     */
    public static boolean onAnimateFluidTick(Level world, BlockPos pos, FluidState state, Random rand) {
        return MinecraftForge.EVENT_BUS.post(new AnimateFluidTickEvent(world, pos, state, rand));
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
