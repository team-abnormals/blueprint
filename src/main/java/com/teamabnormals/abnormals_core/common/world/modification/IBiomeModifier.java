package com.teamabnormals.abnormals_core.common.world.modification;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The interface for representing a biome modifier.
 * <p>This extends {@link Predicate} to check if a {@link BiomeModificationContext} is suitable for modification and {@link Consumer} to use a {@link BiomeModificationContext}. </p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface IBiomeModifier extends Predicate<BiomeModificationContext>, Consumer<BiomeModificationContext> {
}
