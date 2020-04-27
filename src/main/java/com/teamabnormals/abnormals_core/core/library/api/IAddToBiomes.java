package com.teamabnormals.abnormals_core.core.library.api;

import java.util.function.Consumer;

import net.minecraft.world.biome.Biome;

/**
 * Implement this on Features to add them to biomes automatically
 * @author SmellyModder(Luke Tonon)
 */
public interface IAddToBiomes {
	Consumer<Biome> processBiomeAddition();
}