package com.teamabnormals.blueprint.core.events;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This class holds two events related to falling blocks.
 *
 * @author Markus1002
 */
public class FallingBlockEvent extends Event {
    private FallingBlockEntity fallingBlockEntity;

    public FallingBlockEvent(FallingBlockEntity fallingBlockEntity) {
        this.fallingBlockEntity = fallingBlockEntity;
    }

    public FallingBlockEntity getEntity() {
        return this.fallingBlockEntity;
    }

    public void setEntity(FallingBlockEntity fallingBlockEntity) {
        this.fallingBlockEntity = fallingBlockEntity;
    }

    /**
     * Handles the processing of the {@link BlockFallEvent} event.
     */
    public static FallingBlockEntity onBlockFall(Level level, BlockPos pos, BlockState state, FallingBlockEntity fallingBlockEntity) {
        FallingBlockEvent event = new BlockFallEvent(level, pos, state, fallingBlockEntity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getEntity();
    }

    /**
     * Handles the processing of the {@link FallingBlockTickEvent} event.
     */
    public static boolean onFallingBlockTick(FallingBlockEntity fallingBlockEntity) {
        return MinecraftForge.EVENT_BUS.post(new FallingBlockTickEvent(fallingBlockEntity));
    }

    /**
     * This event is fired when a {@link FallingBlockEntity} is about to be spawned in the
     * {@link FallingBlockEntity#fall(Level, BlockPos, BlockState)} method.
     * <p>The event can be used to replace and modify the falling block entity before it spawns.
     * <p>The event is not {@link Cancelable}.
     */
    public static class BlockFallEvent extends FallingBlockEvent {
        private final Level level;
        private final BlockPos pos;
        private final BlockState state;

        public BlockFallEvent(Level level, BlockPos pos, BlockState state, FallingBlockEntity fallingBlockEntity) {
            super(fallingBlockEntity);
            this.level = level;
            this.pos = pos;
            this.state = state;
        }

        public Level getLevel() {
            return this.level;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public BlockState getState() {
            return this.state;
        }
    }

    /**
     * This event is fired when a {@link FallingBlockEntity} is ticked in the {@link FallingBlockEntity#tick()} method.
     * <p>If cancelled, the the falling block entity will not update.
     */
    @Cancelable
    public static class FallingBlockTickEvent extends FallingBlockEvent {
        public FallingBlockTickEvent(FallingBlockEntity fallingBlockEntity) {
            super(fallingBlockEntity);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }
}
