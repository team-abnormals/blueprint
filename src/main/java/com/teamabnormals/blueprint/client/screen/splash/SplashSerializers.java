package com.teamabnormals.blueprint.client.screen.splash;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * The registry class for {@link MapCodec} instances that serialize and deserialize {@link Splash} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class SplashSerializers {
	static final BasicRegistry<MapCodec<? extends Splash>> SPLASH_SERIALIZERS = new BasicRegistry<>();

	static {
		SPLASH_SERIALIZERS.register(ResourceLocation.fromNamespaceAndPath(Blueprint.MOD_ID, "literal"), LiteralSplash.CODEC);
	}

	/**
	 * Registers a {@link MapCodec} instance for serializing and deserializing {@link Splash} instances.
	 * <p>Call this during mod loading on the client-side to add new serializers for custom {@link Splash} implementations.</p>
	 *
	 * @param name  A {@link ResourceLocation} instance to identify the {@link MapCodec} instance.
	 * @param codec A {@link MapCodec} instance for serializing and deserializing {@link Splash} instances.
	 */
	public static synchronized void register(ResourceLocation name, MapCodec<? extends Splash> codec) {
		SPLASH_SERIALIZERS.register(name, codec);
	}
}
