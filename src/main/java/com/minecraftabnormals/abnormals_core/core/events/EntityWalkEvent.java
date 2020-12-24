package com.minecraftabnormals.abnormals_core.core.events;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * This event is fired when an {@link Entity} walks on top of a block. 
 * <p>Cancelling this event will prevent {@link net.minecraft.block.Block#onEntityWalk Block.onEntityWalk()} in the block's class from being called.</p>
 *
 * @author abigailfails
 */
@Cancelable
public final class EntityWalkEvent extends Event {
    private final World world;
    private final BlockPos pos;
    private final Entity entity;

    public EntityWalkEvent(World world, BlockPos pos, Entity entity) {
        this.world = world;
        this.pos = pos;
        this.entity = entity;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Entity getEntity() {
        return this.entity;
    }

    /**
     * Fires the {@link EntityWalkEvent} for a given {@link World}, {@link BlockPos} and {@link Entity}.
     * @param world The {@link World} that the {@code pos} is in.
     * @param pos The {@link BlockPos} that the walked-on block is at.
     * @param entity The {@link Entity} that walked on the block at {@code pos}.
     */
    public static boolean onEntityWalk(World world, BlockPos pos, Entity entity) {
        return MinecraftForge.EVENT_BUS.post(new EntityWalkEvent(world, pos, entity));
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
