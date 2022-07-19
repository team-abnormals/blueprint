package com.teamabnormals.blueprint.client.screen.splash;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * The registry class for {@link Codec} instances that serialize and deserialize {@link Splash} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class SplashSerializers {
	static final BasicRegistry<Codec<? extends Splash>> SPLASH_SERIALIZERS = new BasicRegistry<>();

	static {
		SPLASH_SERIALIZERS.register(new ResourceLocation(Blueprint.MOD_ID, "literal"), LiteralSplash.CODEC);
	}

	/**
	 * Registers a {@link Codec} instance for serializing and deserializing {@link Splash} instances.
	 * <p>Call this during mod loading on the client-side to add new serializers for custom {@link Splash} implementations.</p>
	 *
	 * @param name  A {@link ResourceLocation} instance to identify the {@link Codec} instance.
	 * @param codec A {@link Codec} instance for serializing and deserializing {@link Splash} instances.
	 */
	public static synchronized void register(ResourceLocation name, Codec<? extends Splash> codec) {
		SPLASH_SERIALIZERS.register(name, codec);
	}
}
