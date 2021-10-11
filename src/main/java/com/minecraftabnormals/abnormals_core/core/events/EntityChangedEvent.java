package com.minecraftabnormals.abnormals_core.core.events;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * This event is fired when {@link net.minecraft.server.level.ServerEntity#sendChanges()} gets called.
 *
 * @author SmellyModder (Luke Tonon)
 * @see com.minecraftabnormals.abnormals_core.core.mixin.ServerEntityMixin
 */
public final class EntityChangedEvent extends EntityEvent {
	private final boolean updating;

	private EntityChangedEvent(Entity entity, boolean updating) {
		super(entity);
		this.updating = updating;
	}

	/**
	 * Fires an {@link EntityChangedEvent} instance for a given {@link Entity} and boolean.
	 *
	 * @param entity   The entity getting its changed sent.
	 * @param updating If the entity is updating.
	 */
	public static void onEntitySendChanges(Entity entity, boolean updating) {
		MinecraftForge.EVENT_BUS.post(new EntityChangedEvent(entity, updating));
	}

	/**
	 * Gets if this event was fired when the entity was being updated.
	 *
	 * @return If this was fired when the entity was being updated.
	 * @see net.minecraft.server.level.ServerEntity#sendChanges()
	 * @see com.minecraftabnormals.abnormals_core.core.mixin.ServerEntityMixin
	 */
	public boolean isUpdating() {
		return this.updating;
	}
}
