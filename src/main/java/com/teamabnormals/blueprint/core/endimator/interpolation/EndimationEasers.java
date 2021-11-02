package com.teamabnormals.blueprint.core.endimator.interpolation;

import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Function;

/**
 * A registry class for identifiable functions that specify the rate of change of a parameter over time.
 * <p>This registry is used when serializing and deserializing {@link EndimationInterpolator} instances.</p>
 * <p>Pull Requests to add more of these are welcomed!</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationEasers {
	public static final BasicRegistry<Function<Float, Float>> REGISTRY = new BasicRegistry<>();
	public static final Function<Float, Float> LINEAR = register("linear", Function.identity());
	public static final Function<Float, Float> EASE_IN_SINE = register("ease_in_sine", progress -> 1.0F - (float) Math.cos(Mth.HALF_PI * progress));
	public static final Function<Float, Float> EASE_OUT_SINE = register("ease_out_sine", progress -> (float) Math.sin(Mth.HALF_PI * progress));
	public static final Function<Float, Float> EASE_IN_OUT_SINE = register("ease_in_out_sine", progress -> (float) ((-Math.cos(Math.PI * progress) + 1.0F) * 0.5F));
	public static final Function<Float, Float> EASE_IN_CUBIC = register("ease_in_cubic", progress -> progress * progress * progress);
	public static final Function<Float, Float> EASE_OUT_CUBIC = register("ease_out_cubic", progress -> {
		float base = 1.0F - progress;
		return 1.0F - base * base * base;
	});
	public static final Function<Float, Float> EASE_IN_OUT_CUBIC = register("ease_in_out_cubic", progress -> {
		if (progress < 0.5F) {
			return 4.0F * progress * progress * progress;
		} else {
			float base = -2 * progress + 2;
			return 1.0F - (base * base * base) * 0.5F;
		}
	});

	private EndimationEasers() {
	}

	private static Function<Float, Float> register(String name, Function<Float, Float> easer) {
		REGISTRY.register(new ResourceLocation(name), easer);
		return easer;
	}

	/**
	 * Registers an easing function with a {@link ResourceLocation} name.
	 *
	 * @param name  A {@link ResourceLocation} name for the function.
	 * @param easer An easing function to register.
	 * @return The registered easing function.
	 */
	public static synchronized Function<Float, Float> register(ResourceLocation name, Function<Float, Float> easer) {
		REGISTRY.register(name, easer);
		return easer;
	}
}
