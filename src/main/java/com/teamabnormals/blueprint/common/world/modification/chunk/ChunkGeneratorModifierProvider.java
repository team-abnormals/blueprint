package com.teamabnormals.blueprint.common.world.modification.chunk;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * Subclass of {@link ObjectModifierProvider} to ease creation of data generators for chunk generator modifiers.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class ChunkGeneratorModifierProvider extends ObjectModifierProvider<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>> {

	public ChunkGeneratorModifierProvider(DataGenerator dataGenerator, String modId, RegistryOps<JsonElement> registryOps) {
		super(dataGenerator, modId, true, ChunkGeneratorModificationManager.PATH, ChunkGeneratorModifierSerializers.REGISTRY, registryOps);
	}

	public ChunkGeneratorModifierProvider(DataGenerator dataGenerator, String modId) {
		super(dataGenerator, modId, true, ChunkGeneratorModificationManager.PATH, ChunkGeneratorModifierSerializers.REGISTRY, RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(Registry.REGISTRY)));
	}

}
