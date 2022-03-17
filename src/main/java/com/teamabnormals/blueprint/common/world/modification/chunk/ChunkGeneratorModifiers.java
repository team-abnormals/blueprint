package com.teamabnormals.blueprint.common.world.modification.chunk;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.SurfaceRuleModifier;
import com.teamabnormals.blueprint.core.util.modification.ModifierRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * The registry class for {@link com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.IChunkGeneratorModifier} implementations.
 *
 * @author SmellyModder (Luke Tonon)
 */
//TODO: Add a structure settings modifier
public final class ChunkGeneratorModifiers {
	public static final ModifierRegistry<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>> REGISTRY = new ModifierRegistry<>();

	public static final SurfaceRuleModifier SURFACE_RULE = REGISTRY.register("surface_rule", new SurfaceRuleModifier());
}
