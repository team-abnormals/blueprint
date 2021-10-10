package com.minecraftabnormals.abnormals_core.core.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;


/**
 * This event is fired when an {@link Entity} steps on a block.
 * <p>Cancelling this event will prevent {@link net.minecraft.world.level.block.Block#stepOn} in the block's class from being called.</p>
 *
 * @author abigailfails
 */
@Cancelable
public final class EntityStepEvent extends Event {
	private final Level level;
	private final BlockPos pos;
	private final Entity entity;

	public EntityStepEvent(Level level, BlockPos pos, Entity entity) {
		this.level = level;
		this.pos = pos;
		this.entity = entity;
	}

	/**
	 * Fires the {@link EntityStepEvent} for a given {@link Level}, {@link BlockPos}, and {@link Entity}.
	 *
	 * @param level  The {@link Level} that the {@code pos} is in.
	 * @param pos    The {@link BlockPos} that the stepped-on block is at.
	 * @param entity The {@link Entity} that stepped on the block at {@code pos}.
	 */
	public static boolean onEntityStep(Level level, BlockPos pos, Entity entity) {
		return MinecraftForge.EVENT_BUS.post(new EntityStepEvent(level, pos, entity));
	}

	public Level getLevel() {
		return this.level;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
