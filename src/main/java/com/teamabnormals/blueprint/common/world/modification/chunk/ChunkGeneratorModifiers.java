package com.teamabnormals.blueprint.common.world.modification.chunk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.SurfaceRuleModifier;
import com.teamabnormals.blueprint.core.util.modification.ModifierDataProvider;
import com.teamabnormals.blueprint.core.util.modification.ModifierRegistry;
import net.minecraft.data.DataGenerator;
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
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Creates a new {@link ModifierDataProvider} instance for chunk generator modifiers.
	 *
	 * @param dataGenerator A {@link DataGenerator} to use when generating the configured {@link com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.IChunkGeneratorModifier} instances.
	 * @param name          A name for the provider.
	 * @param modId         The ID of the mod using this provider.
	 * @param registryOps   A {@link RegistryOps} instance for additional serialization usage.
	 * @param toGenerate    An array of {@link ModifierDataProvider.ProviderEntry} instances to generate.
	 * @return A new {@link ModifierDataProvider} instance for chunk generator modifiers.
	 */
	@SafeVarargs
	public static ModifierDataProvider<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>> createDataProvider(DataGenerator dataGenerator, String name, String modId, RegistryOps<JsonElement> registryOps, ModifierDataProvider.ProviderEntry<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>>... toGenerate) {
		return new ModifierDataProvider<>(dataGenerator, name, GSON, modId, "modifiers/dimension/chunk_generator", REGISTRY, registryOps, toGenerate);
	}
}
