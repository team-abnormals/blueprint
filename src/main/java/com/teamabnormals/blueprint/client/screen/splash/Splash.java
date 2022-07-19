package com.teamabnormals.blueprint.client.screen.splash;

import com.mojang.serialization.Codec;
import net.minecraft.client.User;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * The interface for representing a Blueprint splash on the main menu screen.
 * <p>Use {@link #CODEC} and {@link #LIST_CODEC} for serializing and deserializing instances of this interface.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see SplashSerializers
 */
public interface Splash {
	Codec<Splash> CODEC = SplashSerializers.SPLASH_SERIALIZERS.dispatchStable(Splash::codec, Function.identity());
	Codec<List<Splash>> LIST_CODEC = CODEC.listOf();

	/**
	 * Gets the display text for this splash.
	 * <p>Return null to make the system pick a different splash.</p>
	 *
	 * @param user   The {@link User} instance belonging to the user.
	 * @param random A {@link RandomSource} instance to use for any RNG needs.
	 * @return The display text for this splash or null to make the system pick a different splash.
	 */
	@Nullable
	String getText(User user, RandomSource random);

	/**
	 * Gets if this splash is a normal random splash.
	 * <p>If false, this splash will get chosen from a list of other nonrandom splashes.</p>
	 *
	 * @return If this splash is a normal random splash.
	 */
	boolean isRandom();

	/**
	 * Gets the {@link Codec} instance used for serializing this splash.
	 *
	 * @return The {@link Codec} instance used for serializing this splash.
	 */
	Codec<? extends Splash> codec();
}
