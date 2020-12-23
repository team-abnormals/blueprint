package com.minecraftabnormals.abnormals_core.core.events;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * This event is fired before {@link net.minecraft.fluid.Fluid#animateTick  Fluid.animateTick()}. Canceling it
 * prevents the original method from being called.
 *
 * @author abigailfails
 */
@Cancelable
public class AnimateFluidTickEvent extends Event {
    private final World world;
    private final BlockPos pos;
    private final FluidState state;
    private final Random random;
    public AnimateFluidTickEvent(World world, BlockPos pos, FluidState state, Random random) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.random = random;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public FluidState getState() {
        return state;
    }

    public Random getRandom() {
        return random;
    }

    public static boolean onAnimateFluidTick(World world, BlockPos pos, FluidState state, Random random) {
        return MinecraftForge.EVENT_BUS.post(new AnimateFluidTickEvent(world, pos, state, random));
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
