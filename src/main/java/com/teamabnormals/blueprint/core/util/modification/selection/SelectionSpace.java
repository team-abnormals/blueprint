package com.teamabnormals.blueprint.core.util.modification.selection;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * The functional interface used for iterating over a space to select targets from.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface SelectionSpace {
	/**
	 * Iterates over the space, using a {@link Consumer} instance for each resource.
	 *
	 * @param consumer A {@link Consumer} instance to use for processing each resource.
	 */
	void forEach(Consumer<ResourceLocation> consumer);
}
