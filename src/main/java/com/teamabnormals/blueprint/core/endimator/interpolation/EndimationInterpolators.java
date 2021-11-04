package com.teamabnormals.blueprint.core.endimator.interpolation;

import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * A registry class for {@link EndimationInterpolator}s.
 * <p>This registry is used when serializing and deserializing {@link ConfiguredEndimationInterpolator} instances.</p>
 * <p>{@link #REGISTRY} should only get used for looking up values in the registry.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationInterpolators {
	public static final BasicRegistry<EndimationInterpolator<?>> REGISTRY = new BasicRegistry<>();
	public static final LinearEndimationInterpolator LINEAR = register("linear", new LinearEndimationInterpolator());
	public static final CatmullRomEndimationInterpolator CATMULL_ROM = register("catmullrom", new CatmullRomEndimationInterpolator());

	private EndimationInterpolators() {
	}

	private static <C, I extends EndimationInterpolator<C>> I register(String name, I interpolator) {
		REGISTRY.register(new ResourceLocation(name), interpolator);
		return interpolator;
	}

	/**
	 * Registers an {@link EndimationInterpolator} with a given {@link ResourceLocation} name.
	 *
	 * @param name         The {@link ResourceLocation} name for the {@link EndimationInterpolator}.
	 * @param interpolator An {@link EndimationInterpolator} instance to register.
	 * @param <C>          The type of config the {@link EndimationInterpolator} uses.
	 * @param <I>          The type of {@link EndimationInterpolator} to register.
	 * @return The registered {@link EndimationInterpolator}.
	 */
	public static synchronized <C, I extends EndimationInterpolator<C>> I register(ResourceLocation name, I interpolator) {
		REGISTRY.register(name, interpolator);
		return interpolator;
	}
}
