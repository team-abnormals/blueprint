package com.teamabnormals.blueprint.core.events;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * A {@link Event} extension ONLY for automated class loading because NeoForge removed the old technique.
 * <p>This event is never fired.</p>
 */
public final class LoadThisClassEvent extends Event implements IModBusEvent {
}
