package com.teamabnormals.abnormals_core.common.world.modification;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import javax.annotation.Nullable;

/**
 * A container class for holding a {@link BiomeLoadingEvent} with the {@link Biome} that the event was fired for along with the biome's {@link RegistryKey}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeModificationContext {
	public final BiomeLoadingEvent event;
	public final RegistryKey<Biome> registryKey;
	public final Biome biome;

	private BiomeModificationContext(BiomeLoadingEvent event, RegistryKey<Biome> registryKey, Biome biome) {
		this.event = event;
		this.registryKey = registryKey;
		this.biome = biome;
	}

	/**
	 * Tries to create a {@link BiomeModificationContext} for a {@link BiomeLoadingEvent} event.
	 *
	 * @param event The {@link BiomeLoadingEvent} to try to create this instance for.
	 * @return A {@link BiomeModificationContext} for a {@link BiomeLoadingEvent} event or null if the biome that fired the event could not be found.
	 */
	@Nullable
	public static BiomeModificationContext create(BiomeLoadingEvent event) {
		ForgeRegistry<Biome> biomeForgeRegistry = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
		Biome biome = biomeForgeRegistry.getValue(event.getName());
		if (biome != null) {
			return new BiomeModificationContext(event, biomeForgeRegistry.getKey(biomeForgeRegistry.getID(biome)), biome);
		}
		return null;
	}
}
