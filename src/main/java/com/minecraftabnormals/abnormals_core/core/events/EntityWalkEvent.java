package com.minecraftabnormals.abnormals_core.core.events;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired when an {@link Entity} walks on top of a block. Cancelling it
 * prevents {@link net.minecraft.block.Block#onEntityWalk Block.onEntityWalk()} in the block's class from being called.
 *
 * @author abigailfails
 */
@Cancelable
public class EntityWalkEvent extends Event {
    private final World world;
    private final BlockPos pos;
    private final Entity entity;

    public EntityWalkEvent(World world, BlockPos pos, Entity entity) {
        this.world = world;
        this.pos = pos;
        this.entity = entity;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Entity getEntity() {
        return entity;
    }

    public static boolean onEntityWalk(World world, BlockPos pos, Entity entity) {
        return MinecraftForge.EVENT_BUS.post(new EntityWalkEvent(world, pos, entity));
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
