package com.teamabnormals.blueprint.client.screen.splash;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.User;
import net.minecraft.util.RandomSource;

/**
 * The record implementation of {@link Splash} for simple random splashes.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public record LiteralSplash(String text) implements Splash {
	public static final Codec<LiteralSplash> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.STRING.fieldOf("text").forGetter(splash -> splash.text)
		).apply(instance, LiteralSplash::new);
	});

	@Override
	public String getText(User user, RandomSource random) {
		return this.text;
	}

	@Override
	public boolean isRandom() {
		return true;
	}

	@Override
	public Codec<? extends Splash> codec() {
		return CODEC;
	}
}
