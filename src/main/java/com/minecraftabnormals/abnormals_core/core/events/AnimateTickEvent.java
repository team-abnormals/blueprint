package com.minecraftabnormals.abnormals_core.core.events;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import java.util.Random;

/**
 * This event is fired before {@link net.minecraft.block.Block#animateTick Block.animateTick()}. 
 * <p>Canceling this event will prevent the original method from being called.</p>
 *
 * @author abigailfails
 */
@Cancelable
public final class AnimateTickEvent extends BlockEvent {
    private final Random random;

    public AnimateTickEvent(BlockState state, World world, BlockPos pos, Random rand) {
        super(world, pos, state);
        this.random = rand;
    }

    public Random getRandom() {
        return this.random;
    }

    /**
     * Fires the {@link AnimateTickEvent} for a given {@link BlockState}, {@link World}, {@link BlockPos} and {@link Random}.
     * @param state The {@link BlockState} that {@link net.minecraft.block.Block#animateTick Block.animateTick()} is being fired for.
     * @param world The {@link World} that the {@code state} is in.
     * @param pos The {@link BlockPos} that the {@code state} is at.
     * @param rand The {@link Random} to be used for randomizing particle placement.
     */
    public static boolean onAnimateTick(BlockState state, World world, BlockPos pos, Random rand) {
        return MinecraftForge.EVENT_BUS.post(new AnimateTickEvent(state, world, pos, rand));
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
