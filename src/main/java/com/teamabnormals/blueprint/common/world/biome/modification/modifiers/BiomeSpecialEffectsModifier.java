package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.mixin.BiomeSpecialEffectsInvokerMixin;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.Optional;

/**
 * An {@link IBiomeModifier} implementation that modifies the {@link BiomeSpecialEffects} of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeSpecialEffectsModifier implements IBiomeModifier<BiomeSpecialEffectsModifier.Config> {
	private static final Codec<Unit> EMPTY = Codec.unit(Unit.INSTANCE);
	private static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.INT.optionalFieldOf("fog_color").forGetter(config -> config.fogColor),
				Codec.INT.optionalFieldOf("water_color").forGetter(config -> config.waterColor),
				Codec.INT.optionalFieldOf("water_fog_color").forGetter(config -> config.waterFogColor),
				Codec.INT.optionalFieldOf("sky_color").forGetter(config -> config.skyColor),
				Codec.either(Codec.INT, EMPTY).optionalFieldOf("foliage_color").forGetter(config -> config.foliageColorOverride),
				Codec.either(Codec.INT, EMPTY).optionalFieldOf("grass_color").forGetter(config -> config.grassColorOverride),
				BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier").forGetter(config -> config.grassColorModifier),
				Codec.either(AmbientParticleSettings.CODEC, EMPTY).optionalFieldOf("particle").forGetter(config -> config.ambientParticleSettings),
				Codec.either(SoundEvent.CODEC, EMPTY).optionalFieldOf("ambient_sound").forGetter(config -> config.ambientLoopSoundEvent),
				Codec.either(AmbientMoodSettings.CODEC, EMPTY).optionalFieldOf("mood_sound").forGetter(config -> config.ambientMoodSettings),
				Codec.either(AmbientAdditionsSettings.CODEC, EMPTY).optionalFieldOf("additions_sound").forGetter(config -> config.ambientAdditionsSettings),
				Codec.either(Music.CODEC, EMPTY).optionalFieldOf("music").forGetter(config -> config.backgroundMusic)
		).apply(instance, Config::new);
	});

	private static <T> Optional<T> mapEitherOrElse(Optional<Either<T, Unit>> optional, Optional<T> orElse) {
		if (optional.isEmpty()) return orElse;
		return optional.get().map(Optional::of, unit -> Optional.empty());
	}

	@Override
	public void modify(BiomeLoadingEvent event, Config config) {
		BiomeSpecialEffects specialEffects = event.getEffects();
		event.setEffects(BiomeSpecialEffectsInvokerMixin.init(config.fogColor.orElse(specialEffects.getFogColor()), config.waterColor.orElse(specialEffects.getWaterColor()), config.waterFogColor.orElse(specialEffects.getWaterFogColor()), config.skyColor.orElse(specialEffects.getSkyColor()), mapEitherOrElse(config.foliageColorOverride, specialEffects.getFoliageColorOverride()), mapEitherOrElse(config.grassColorOverride, specialEffects.getGrassColorOverride()), config.grassColorModifier.orElse(specialEffects.getGrassColorModifier()), mapEitherOrElse(config.ambientParticleSettings, specialEffects.getAmbientParticleSettings()), mapEitherOrElse(config.ambientLoopSoundEvent, specialEffects.getAmbientLoopSoundEvent()), mapEitherOrElse(config.ambientMoodSettings, specialEffects.getAmbientMoodSettings()), mapEitherOrElse(config.ambientAdditionsSettings, specialEffects.getAmbientAdditionsSettings()), mapEitherOrElse(config.backgroundMusic, specialEffects.getBackgroundMusic())));
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

	public static record Config(Optional<Integer> fogColor, Optional<Integer> waterColor,
								Optional<Integer> waterFogColor, Optional<Integer> skyColor,
								Optional<Either<Integer, Unit>> foliageColorOverride,
								Optional<Either<Integer, Unit>> grassColorOverride,
								Optional<BiomeSpecialEffects.GrassColorModifier> grassColorModifier,
								Optional<Either<AmbientParticleSettings, Unit>> ambientParticleSettings,
								Optional<Either<SoundEvent, Unit>> ambientLoopSoundEvent,
								Optional<Either<AmbientMoodSettings, Unit>> ambientMoodSettings,
								Optional<Either<AmbientAdditionsSettings, Unit>> ambientAdditionsSettings,
								Optional<Either<Music, Unit>> backgroundMusic) {
	}
}
