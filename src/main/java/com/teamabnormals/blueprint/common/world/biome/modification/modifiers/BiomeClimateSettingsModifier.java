package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.Optional;

/**
 * An {@link IBiomeModifier} implementation that modifies the {@link Biome.ClimateSettings} of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeClimateSettingsModifier implements IBiomeModifier<BiomeClimateSettingsModifier.Config> {
	private static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Biome.Precipitation.CODEC.optionalFieldOf("precipitation").forGetter(config -> config.precipitation),
				Codec.FLOAT.optionalFieldOf("temperature").forGetter(config -> config.temperature),
				Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier").forGetter(config -> config.temperatureModifier),
				Codec.FLOAT.optionalFieldOf("downfall").forGetter(config -> config.downfall)
		).apply(instance, Config::new);
	});

	@Override
	public void modify(BiomeLoadingEvent event, Config config) {
		Biome.ClimateSettings climateSettings = event.getClimate();
		event.setClimate(new Biome.ClimateSettings(config.precipitation.orElse(climateSettings.precipitation), config.temperature.orElse(climateSettings.temperature), config.temperatureModifier.orElse(climateSettings.temperatureModifier), config.downfall.orElse(climateSettings.downfall)));
	}

	@Override
	public JsonElement serialize(Config config, RegistryWriteOps<JsonElement> additional) throws JsonParseException {
		var result = CODEC.encodeStart(additional, config);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return result.result().get();
	}

	@Override
	public Config deserialize(JsonElement element, RegistryReadOps<JsonElement> additional) throws JsonParseException {
		var result = CODEC.decode(additional, element);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return result.result().get().getFirst();
	}

	public static record Config(Optional<Biome.Precipitation> precipitation, Optional<Float> temperature, Optional<Biome.TemperatureModifier> temperatureModifier, Optional<Float> downfall) {
	}
}
