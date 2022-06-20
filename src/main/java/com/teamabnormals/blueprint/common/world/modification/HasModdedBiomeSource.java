package com.teamabnormals.blueprint.common.world.modification;

import javax.annotation.Nullable;

/**
 * The interface used for adding methods to handle storage of {@link ModdedBiomeSource} instances in {@link net.minecraft.world.level.levelgen.WorldGenerationContext} instances.
 *
 * @author SmellyModder (Luke Tonon)
 * @see com.teamabnormals.blueprint.core.mixin.WorldGenerationContextMixin
 */
public interface HasModdedBiomeSource {
	/**
	 * Gets the {@link ModdedBiomeSource} instance being stored.
	 *
	 * @return The {@link ModdedBiomeSource} instance being stored.
	 */
	@Nullable
	ModdedBiomeSource getModdedBiomeSource();

	/**
	 * Sets the source.
	 *
	 * @param moddedBiomeSource The new {@link ModdedBiomeSource} instance to use.
	 */
	void setModdedBiomeSource(@Nullable ModdedBiomeSource moddedBiomeSource);
}
