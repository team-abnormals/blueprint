package com.teamabnormals.blueprint.core.util.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * A basic {@link AbstractSubRegistryHelper} for sounds. This contains some useful registering methods for sounds.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class SoundSubRegistryHelper extends AbstractSubRegistryHelper<SoundEvent, DeferredRegister<SoundEvent>> {

	public SoundSubRegistryHelper(RegistryHelper parent, DeferredRegister<SoundEvent> deferredRegister) {
		super(parent, deferredRegister);
	}

	public SoundSubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(Registries.SOUND_EVENT, parent.getModId()));
	}

	/**
	 * Creates and registers a {@link SoundEvent}.
	 *
	 * @param name The sound's name.
	 * @return A {@link DeferredHolder} containing the created {@link SoundEvent}.
	 */
	public DeferredHolder<SoundEvent, SoundEvent> createSoundEvent(String name) {
		return this.deferredRegister.register(name, () -> SoundEvent.createVariableRangeEvent(this.parent.prefix(name)));
	}

}
