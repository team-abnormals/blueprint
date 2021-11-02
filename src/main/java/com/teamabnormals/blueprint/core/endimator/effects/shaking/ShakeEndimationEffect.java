package com.teamabnormals.blueprint.core.endimator.effects.shaking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.endimator.effects.EndimationEffect;
import com.teamabnormals.blueprint.core.endimator.effects.EndimationEffectSource;

/**
 * An {@link EndimationEffect} extension for processing screen shaking effects.
 *
 * @see com.teamabnormals.blueprint.client.screen.shaking.ScreenShakeHandler
 * @author SmellyModder (Luke Tonon)
 */
public final class ShakeEndimationEffect extends EndimationEffect<ShakeEndimationEffect.Config> {

	public ShakeEndimationEffect() {
		super(Config.CODEC);
	}

	@Override
	public void process(EndimationEffectSource source, float time, Config config) {
		source.processShake(config);
	}

	public static record Config(int duration, double intensityX, double intensityY, double intensityZ, double maxBuildupX, double maxBuildupY, double maxBuildupZ, double decayX, double decayY, double decayZ) {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(
					Codec.INT.fieldOf("duration").forGetter(config -> config.duration),
					Codec.DOUBLE.fieldOf("x_intensity").forGetter(config -> config.intensityX),
					Codec.DOUBLE.fieldOf("y_intensity").forGetter(config -> config.intensityY),
					Codec.DOUBLE.fieldOf("z_intensity").forGetter(config -> config.intensityZ),
					Codec.DOUBLE.fieldOf("x_max_buildup").forGetter(config -> config.maxBuildupX),
					Codec.DOUBLE.fieldOf("y_max_buildup").forGetter(config -> config.maxBuildupY),
					Codec.DOUBLE.fieldOf("z_max_buildup").forGetter(config -> config.maxBuildupZ),
					Codec.DOUBLE.optionalFieldOf("x_decay", 0.98D).forGetter(config -> config.decayX),
					Codec.DOUBLE.optionalFieldOf("y_decay", 0.98D).forGetter(config -> config.decayY),
					Codec.DOUBLE.optionalFieldOf("z_decay", 0.98D).forGetter(config -> config.decayZ)
			).apply(instance, Config::new);
		});
	}

}
