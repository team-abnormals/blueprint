package com.minecraftabnormals.abnormals_core.core.events;

import com.minecraftabnormals.abnormals_core.core.mixin.ServerEntityMixin;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * This event is fired when a {@link net.minecraft.server.level.ServerEntity} ticks.
 * @author SmellyModder (Luke Tonon)
 */
public final class EntityTrackingEvent extends EntityEvent {
	private final boolean updating;

	private EntityTrackingEvent(Entity entity, boolean updating) {
		super(entity);
		this.updating = updating;
	}

	public static void onEntityTracking(Entity entity, boolean updating) {
		MinecraftForge.EVENT_BUS.post(new EntityTrackingEvent(entity, updating));
	}

	/**
	 * @return If this was fired when the entity was being updated
	 * @see net.minecraft.server.level.ServerEntity#sendChanges()
	 * @see ServerEntityMixin
	 */
	public boolean isUpdating() {
		return this.updating;
	}
}
