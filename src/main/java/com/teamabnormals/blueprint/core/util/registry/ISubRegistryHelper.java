package com.teamabnormals.blueprint.core.util.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

/**
 * An interface for 'sub' registry helpers used in {@link RegistryHelper}.
 *
 * @param <T> The type of objects this helper registers.
 * @author SmellyModder (Luke Tonon)
 */
public interface ISubRegistryHelper<T> {
	/**
	 * @return The {@link RegistryHelper} this is a child of.
	 */
	RegistryHelper getParent();

	/**
	 * @return The {@link DeferredRegister} for registering.
	 */
	DeferredRegister<T> getDeferredRegister();

	/**
	 * Should ideally register {@link #getDeferredRegister()}.
	 *
	 * @param eventBus The event bus to register this to.
	 */
	void register(IEventBus eventBus);
}