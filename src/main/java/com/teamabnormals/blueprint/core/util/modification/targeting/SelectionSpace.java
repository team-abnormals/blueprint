package com.teamabnormals.blueprint.core.util.modification.targeting;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

/**
 * The functional interface used for iterating over a space to select targets from.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface SelectionSpace {
	/**
	 * Iterates over the space, using a {@link BiConsumer} instance for each entry.
	 * <p>It is possible for an entry to have a {@link com.google.gson.JsonNull#INSTANCE} as its {@link JsonElement}.</p>
	 *
	 * @param consumer A {@link BiConsumer} instance to use for processing each entry.
	 */
	void forEach(BiConsumer<ResourceLocation, JsonElement> consumer);
}
