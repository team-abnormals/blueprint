package com.teamabnormals.blueprint.core.endimator.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * The abstract class that represents a configurable, processable client-side animation effect for {@link EndimationEffectSource} instances.
 *
 * @param <C> The type of config object the effect will use.
 * @author SmellyModder (Luke Tonon)
 * @see EndimationEffectSource
 */
public abstract class EndimationEffect<C> {
	private final Codec<ConfiguredEndimationEffect<C, EndimationEffect<C>>> codec;

	protected EndimationEffect(Codec<C> codec) {
		this.codec = RecordCodecBuilder.create((instance) -> {
			return instance.group(
					codec.fieldOf("config").forGetter(ConfiguredEndimationEffect::getConfig),
					Codec.FLOAT.fieldOf("time").forGetter(ConfiguredEndimationEffect::getTime)
			).apply(instance, (config, time) -> new ConfiguredEndimationEffect<>(this, config, time));
		});
	}

	/**
	 * Processes this effect on a given {@link EndimationEffectSource} instance at a given time using a given config.
	 *
	 * @param source An {@link EndimationEffectSource} instance to process the effect on.
	 * @param time   The time (in seconds) the effect is getting processed at.
	 * @param config A config object to configure how the effect will get processed.
	 */
	public abstract void process(EndimationEffectSource source, float time, C config);

	/**
	 * Gets the {@link #codec} used for serialization and deserialization of {@link ConfiguredEndimationEffect} instances of this type.
	 *
	 * @return The {@link #codec}.
	 */
	public Codec<ConfiguredEndimationEffect<C, EndimationEffect<C>>> getCodec() {
		return this.codec;
	}
}
