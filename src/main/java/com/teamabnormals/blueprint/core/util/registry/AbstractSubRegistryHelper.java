package com.teamabnormals.blueprint.core.util.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;

/**
 * An abstract implementation class of {@link ISubRegistryHelper}.
 * This contains a {@link RegistryHelper} parent and a {@link DeferredRegister} to register objects.
 * <p> It is recommended you use this for making a new {@link ISubRegistryHelper}. </p>
 *
 * @param <T> The type of objects this helper registers.
 * @author SmellyModder (Luke Tonon)
 * @see ISubRegistryHelper
 */
public abstract class AbstractSubRegistryHelper<T> implements ISubRegistryHelper<T> {
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
