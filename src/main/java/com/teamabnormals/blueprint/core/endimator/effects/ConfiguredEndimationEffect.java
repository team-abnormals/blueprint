package com.teamabnormals.blueprint.core.endimator.effects;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.endimator.Endimatable;

/**
 * The class that represents a configured {@link EndimationEffect}.
 * <p>Use {@link #CODEC} for serialization and deserialization of instances of this class.</p>
 *
 * @param <C> The type of config object to use.
 * @param <E> The type of {@link EndimationEffect} to process.
 * @author SmellyModder (Luke Tonon)
 */
public final class ConfiguredEndimationEffect<C, E extends EndimationEffect<C>> implements Comparable<ConfiguredEndimationEffect<?, ?>> {
	public static final Codec<ConfiguredEndimationEffect<?, ?>> CODEC = EndimationEffects.REGISTRY.dispatch(ConfiguredEndimationEffect::getEffect, EndimationEffect::getCodec);
	private final E effect;
	private final C config;
	private final float time;

	public ConfiguredEndimationEffect(E effect, C config, float time) {
		this.effect = effect;
		this.config = config;
		this.time = time;
	}

	/**
	 * Processes the internal {@link #effect} on an {@link Endimatable} instance at a given time.
	 *
	 * @param source An {@link EndimationEffectSource} instance to process the effect on.
	 * @param time   The time (in seconds) the effect is getting processed at.
	 */
	public void process(EndimationEffectSource source, float time) {
		this.effect.process(source, time, this.config);
	}

	/**
	 * Gets the internal {@link #effect}.
	 *
	 * @return The internal {@link #effect}.
	 */
	public E getEffect() {
		return this.effect;
	}

	/**
	 * Gets the {@link #config} for the effect.
	 *
	 * @return The {@link #config} for the effect.
	 */
	public C getConfig() {
		return this.config;
	}

	/**
	 * Gets the {@link #time} the effect should get processed at.
	 *
	 * @return The {@link #time} the effect should get processed at.
	 */
	public float getTime() {
		return this.time;
	}

	@Override
	public int compareTo(ConfiguredEndimationEffect<?, ?> other) {
		return Float.compare(this.time, other.time);
	}
}
