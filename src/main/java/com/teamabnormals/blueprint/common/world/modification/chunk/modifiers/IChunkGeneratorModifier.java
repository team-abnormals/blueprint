package com.teamabnormals.blueprint.common.world.modification.chunk.modifiers;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.IModifier;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * An {@link IModifier} subclass, typed to be used on {@link ChunkGenerator} instances.
 *
 * @param <C> The type of config object for this modifier.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 */
public interface IChunkGeneratorModifier<C> extends IModifier<ChunkGenerator, C, RegistryWriteOps<JsonElement>, RegistryReadOps<JsonElement>> {
}
