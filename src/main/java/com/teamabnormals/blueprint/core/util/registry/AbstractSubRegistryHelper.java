package com.teamabnormals.blueprint.core.util.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * An abstract implementation class of {@link ISubRegistryHelper}.
 * This contains a {@link RegistryHelper} parent and a {@link DeferredRegister} to register objects.
 * <p> It is recommended you use this for making a new {@link ISubRegistryHelper}. </p>
 *
 * @param <T> The type of objects this helper registers.
 * @param <R> The type of {@link DeferredRegister} this helper uses.
 * @author SmellyModder (Luke Tonon)
 * @see ISubRegistryHelper
 */
public abstract class AbstractSubRegistryHelper<T, R extends DeferredRegister<T>> implements ISubRegistryHelper<T> {
	protected final RegistryHelper parent;
	protected final R deferredRegister;

	public AbstractSubRegistryHelper(RegistryHelper parent, R deferredRegister) {
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
	public R getDeferredRegister() {
		return this.deferredRegister;
	}

	/**
	 * Registers this {@link AbstractSubRegistryHelper}.
	 *
	 * @param eventBus The event bus to register this to.
	 */
	@Override
	public void register(IEventBus eventBus) {
		this.getDeferredRegister().register(eventBus);
	}
	
	/**
	 * Determines whether a group of mods are loaded.
	 *
	 * @param modIds The mod ids of the mods to check.
	 * @return A boolean representing whether all the mods passed in are loaded.
	 */
	public static boolean areModsLoaded(String... modIds) {
		if ("true".equals(System.getProperty("blueprint.indev")))
			return true;
		ModList modList = ModList.get();
		for (String mod : modIds)
			if (!modList.isLoaded(mod))
				return false;
		return true;
	}
}
