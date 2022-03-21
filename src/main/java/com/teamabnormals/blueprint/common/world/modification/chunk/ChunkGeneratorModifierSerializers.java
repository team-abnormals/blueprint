package com.teamabnormals.blueprint.common.world.modification.chunk;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.ChunkGeneratorModifier;
import com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.SurfaceRuleModifier;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierSerializerRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * The registry class for {@link ChunkGeneratorModifier.Serializer} types.
 *
 * @author SmellyModder (Luke Tonon)
 */
//TODO: Possibly add a structure settings modifier
public final class ChunkGeneratorModifierSerializers {
	public static final ObjectModifierSerializerRegistry<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>> REGISTRY = new ObjectModifierSerializerRegistry<>();

	public static final SurfaceRuleModifier.Serializer SURFACE_RULE = REGISTRY.register("surface_rule", new SurfaceRuleModifier.Serializer());
}
