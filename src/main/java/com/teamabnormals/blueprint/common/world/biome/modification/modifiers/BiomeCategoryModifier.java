package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;

/**
 * An {@link IBiomeModifier} implementation that modifies the {@link net.minecraft.world.level.biome.Biome.BiomeCategory} of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeCategoryModifier implements IBiomeModifier<Biome.BiomeCategory> {

	@Override
	public void modify(BiomeLoadingEvent event, Biome.BiomeCategory config) {
		event.setCategory(config);
	}

	@Override
	public JsonElement serialize(Biome.BiomeCategory config, RegistryWriteOps<JsonElement> additional) throws JsonParseException {
		return new JsonPrimitive(config.getSerializedName());
	}

	@Override
	public Biome.BiomeCategory deserialize(JsonElement element, RegistryReadOps<JsonElement> additional) throws JsonParseException {
		var result = Biome.BiomeCategory.CODEC.decode(additional, element);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return result.result().get().getFirst();
	}

}
