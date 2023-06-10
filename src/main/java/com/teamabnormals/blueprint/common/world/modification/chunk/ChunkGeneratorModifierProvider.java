package com.teamabnormals.blueprint.common.world.modification.chunk;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.concurrent.CompletableFuture;

/**
 * Subclass of {@link ObjectModifierProvider} to ease creation of data generators for chunk generator modifiers.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class ChunkGeneratorModifierProvider extends ObjectModifierProvider<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>> {

	public ChunkGeneratorModifierProvider(String modId, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(modId, true, ChunkGeneratorModificationManager.PATH, ChunkGeneratorModifierSerializers.REGISTRY, (ops, group) -> ops, output, lookupProvider);
	}

}
