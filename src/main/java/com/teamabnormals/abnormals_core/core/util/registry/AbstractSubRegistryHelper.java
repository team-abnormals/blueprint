package com.teamabnormals.abnormals_core.core.util.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * An abstract implementation class of {@link ISubRegistryHelper}.
 * This contains a {@link RegistryHelper} parent and a {@link DeferredRegister} to register objects.
 * <p> It is recommended you use this for making a new {@link ISubRegistryHelper}. </p>
 *
 * @param <T> The type of {@link IForgeRegistryEntry} to register objects for.
 * @see ISubRegistryHelper
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class AbstractSubRegistryHelper<T extends IForgeRegistryEntry<T>> implements ISubRegistryHelper<T> {
	protected final RegistryHelper parent;
	protected final DeferredRegister<T> deferredRegister;

	public AbstractSubRegistryHelper(RegistryHelper parent, DeferredRegister<T> deferredRegister) {
		this.parent = parent;
		this.deferredRegister = deferredRegister;
	}

	/**
	 * @return The parent {@link RegistryHelper} this is a child of.
	 */
	@Override
	public RegistryHelper getParent() {
		return this.parent;
	}

	/**
	 * @return The {@link DeferredRegister} belonging to this {@link AbstractSubRegistryHelper}.
	 */
	@Override
	public DeferredRegister<T> getDeferredRegister() {
		return this.deferredRegister;
	}

	/**
	 * Registers the {@link #getDeferredRegister()}.
	 * @param eventBus The event bus to register this to.
	 */
	@Override
	public final void register(IEventBus eventBus) {
		this.getDeferredRegister().register(eventBus);
	}
}
