package com.teamabnormals.abnormals_core.core.util.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * An interface for 'sub' registry helpers used in {@link RegistryHelper}.
 *
 * @param <T> The type of {@link IForgeRegistryEntry} this is for.
 * @author SmellyModder (Luke Tonon)
 */
public interface ISubRegistryHelper<T extends IForgeRegistryEntry<T>> {
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