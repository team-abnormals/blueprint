package com.teamabnormals.blueprint.common.world.storage.receiver;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

/**
 * A class for creating and quickly accessing a level specific {@link NormalNoise} instance.
 * <p>This class is primarily designed to allow a {@link Feature} to use the same {@link NormalNoise} instance
 * every time it is placed in the same Level.
 */
public class LevelNoiseReceiver extends LevelDataReceiver<NormalNoise> {
	private final WorldgenRandom.Algorithm algorithm;
	private final ResourceKey<NoiseParameters> noiseParameters;

	public LevelNoiseReceiver(WorldgenRandom.Algorithm algorithm, ResourceKey<NoiseParameters> noiseParameters) {
		super();
		this.algorithm = algorithm;
		this.noiseParameters = noiseParameters;
	}

	@Override
	public NormalNoise create(ServerLevel level) {
		Registry<NoiseParameters> noise = level.registryAccess().registryOrThrow(Registries.NOISE);
		return NormalNoise.create(this.algorithm.newInstance(level.getSeed()).forkPositional().fromHashOf(this.noiseParameters.location()), noise.getOrThrow(this.noiseParameters));
	}
}