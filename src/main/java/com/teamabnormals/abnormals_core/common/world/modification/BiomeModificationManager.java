package com.teamabnormals.abnormals_core.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

/**
 * Core instance for adding {@link IBiomeModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum BiomeModificationManager {
	INSTANCE;

	private final Set<IBiomeModifier> biomeModifiers = Sets.newHashSet();

	BiomeModificationManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Adds a {@link IBiomeModifier}.
	 * <p>This is thread-safe, so it is safe to call during common setup of your mod.</p>
	 *
	 * @param biomeModifier The {@link IBiomeModifier} to add.
	 */
	public synchronized void addModifier(IBiomeModifier biomeModifier) {
		this.biomeModifiers.add(biomeModifier);
	}

	@SubscribeEvent
	public void onBiomeLoad(BiomeLoadingEvent event) {
		BiomeModificationContext context = BiomeModificationContext.create(event);
		if (context != null) {
			for (IBiomeModifier modifier : this.biomeModifiers) {
				if (modifier.test(context)) {
					modifier.accept(context);
				}
			}
		}
	}
}
