package com.teamabnormals.blueprint.common.world.modification.chunk.modifiers;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * A {@link ObjectModifier} implementation, typed to be used on {@link ChunkGenerator} instances.
 *
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifier
 */
public interface ChunkGeneratorModifier<M extends ChunkGeneratorModifier<M>> extends ObjectModifier<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>, M> {
	/**
	 * A {@link ObjectModifier.Serializer} extension, typed to be used for {@link ChunkGeneratorModifier} types.
	 *
	 * @param <M> The type of {@link ChunkGeneratorModifier} instances to serialize and deserialize.
	 * @author SmellyModder (Luke Tonon)
	 */
	interface Serializer<M extends ChunkGeneratorModifier<M>> extends ObjectModifier.Serializer<M, RegistryOps<JsonElement>, RegistryOps<JsonElement>> {}
}
