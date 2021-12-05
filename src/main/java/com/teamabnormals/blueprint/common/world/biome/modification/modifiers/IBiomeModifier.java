package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.teamabnormals.blueprint.core.util.modification.IModifier;
import net.minecraftforge.event.world.BiomeLoadingEvent;

/**
 * An interface extending the {@link IModifier} interface, typed to be used on loading biomes.
 *
 * @param <C> The type of config object for this modifier.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 */
public interface IBiomeModifier<C> extends IModifier<BiomeLoadingEvent, C, Void, Void> {
}
