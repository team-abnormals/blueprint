package com.teamabnormals.blueprint.core.endimator.interpolation;

import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Function;

/**
 * A registry class for identifiable functions that specify the rate of change of a parameter over time.
 * <p>This registry is used when serializing and deserializing {@link InterpolationType} instances.</p>
 * <p>Pull Requests to add more of these are welcomed!</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @author ebo2022
 */
public final class EndimationEasers {
	public static final BasicRegistry<Function<Float, Float>> REGISTRY = new BasicRegistry<>();
	public static final Function<Float, Float> LINEAR = register("linear", Function.identity());
	public static final Function<Float, Float> EASE_IN_SINE = register("ease_in_sine", progress -> 1.0F - (float) Math.cos(Mth.HALF_PI * progress));
	public static final Function<Float, Float> EASE_OUT_SINE = register("ease_out_sine", progress -> (float) Math.sin(Mth.HALF_PI * progress));
	public static final Function<Float, Float> EASE_IN_OUT_SINE = register("ease_in_out_sine", progress -> (float) ((-Math.cos(Mth.PI * progress) + 1.0F) * 0.5F));
	public static final Function<Float, Float> EASE_IN_QUAD = register("ease_in_quad", progress -> progress * progress);
	public static final Function<Float, Float> EASE_OUT_QUAD = register("ease_out_quad", progress -> {
		float base = 1.0F - progress;
		return 1.0F - base * base;
	});
	public static final Function<Float, Float> EASE_IN_OUT_QUAD = register("ease_in_out_quad", progress -> {
		if (progress < 0.5F) {
			return 2.0F * progress * progress;
		} else {
			float base = -2 * progress + 2;
			return 1.0F - (base * base) * 0.5F;
		}
	});
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
	public static final Function<Float, Float> EASE_IN_QUART = register("ease_in_quart", progress -> progress * progress * progress * progress);
	public static final Function<Float, Float> EASE_OUT_QUART = register("ease_out_quart", progress -> {
		float base = 1.0F - progress;
		return 1.0F - base * base * base * base;
	});
	public static final Function<Float, Float> EASE_IN_OUT_QUART = register("ease_in_out_quart", progress -> {
		if (progress < 0.5F) {
			return 8.0F * progress * progress * progress * progress;
		} else {
			float base = -2 * progress + 2;
			return 1.0F - (base * base * base * base) * 0.5F;
		}
	});
	public static final Function<Float, Float> EASE_IN_QUINT = register("ease_in_quint", progress -> progress * progress * progress * progress * progress);
	public static final Function<Float, Float> EASE_OUT_QUINT = register("ease_out_quint", progress -> {
		float base = 1.0F - progress;
		return 1.0F - base * base * base * base * base;
	});
	public static final Function<Float, Float> EASE_IN_OUT_QUINT = register("ease_in_out_quint", progress -> {
		if (progress < 0.5F) {
			return 16.0F * progress * progress * progress * progress * progress;
		} else {
			float base = -2 * progress + 2;
			return 1.0F - (base * base * base * base * base) * 0.5F;
		}
	});
	public static final Function<Float, Float> EASE_IN_EXPO = register("ease_in_expo", progress -> progress == 0.0F ? 0.0F : (float) Math.pow(2, 10 * progress - 10));
	public static final Function<Float, Float> EASE_OUT_EXPO = register("ease_out_expo", progress -> progress == 1.0F ? 1.0F : 1.0F - (float) Math.pow(2, -10 * progress));
	public static final Function<Float, Float> EASE_IN_OUT_EXPO = register("ease_in_out_expo", progress -> {
		if (progress == 0F) {
			return 0F;
		} else if (progress == 1F) {
			return 1F;
		} else if (progress < 0.5F) {
			return (float) Math.pow(2, 20 * progress - 10) * 0.5F;
		} else {
			return (float) (2 - Math.pow(2, -20 * progress + 10)) * 0.5F;
		}
	});
	public static final Function<Float, Float> EASE_IN_CIRC = register("ease_in_circ", progress -> (float) (1F - Math.sqrt(1 - progress * progress)));
	public static final Function<Float, Float> EASE_OUT_CIRC = register("ease_out_circ", progress -> {
		float base = progress - 1.0F;
		return (float) Math.sqrt(1 - base * base);
	});
	public static final Function<Float, Float> EASE_IN_OUT_CIRC = register("ease_in_out_circ", progress -> {
		if (progress < 0.5F) {
			float base = 2 * progress;
			return (float) ((1 - Math.sqrt(1 - base * base)) * 0.5F);
		} else {
			float base = -2 * progress + 2;
			return (float) (Math.sqrt(1 - base * base) + 1) * 0.5F;
		}
	});
	public static final Function<Float, Float> EASE_IN_BACK = register("ease_in_back", progress -> {
		float bounce = 1.70158F;
		float base = bounce + 1.0F;
		return base * progress * progress * progress - bounce * progress * progress;
	});
	public static final Function<Float, Float> EASE_OUT_BACK = register("ease_out_back", progress -> {
		float bounce = 1.70158F;
		float base = bounce + 1.0F;
		float base2 = progress - 1.0F;
		return 1.0F + base * base2 * base2 * base2 + bounce * base2 * base2;
	});
	public static final Function<Float, Float> EASE_IN_OUT_BACK = register("ease_in_out_back", progress -> {
		float bounce = 1.70158F;
		float base = bounce * 1.525F;
		if (progress < 0.5F) {
			float base2 = 2 * progress;
			return (base2 * base2 * ((base + 1) * 2 * progress - base)) * 0.5F;
		} else {
			float base2 = 2 * progress - 2;
			return (base2 * base2 * ((base + 1) * (progress * 2 - 2) + base2) + 2) * 0.5F;
		}
	});
	public static final Function<Float, Float> EASE_IN_ELASTIC = register("ease_in_elastic", progress -> {
		float base = Mth.TWO_PI / 3.0F;
		if (progress == 0.0F) {
			return 0.0F;
		} else if (progress == 1.0F) {
			return 1.0F;
		} else {
			return (float) (-Math.pow(2, 10 * progress - 10) * Math.sin((progress * 10 - 10.75F) * base));
		}
	});
	public static final Function<Float, Float> EASE_OUT_ELASTIC = register("ease_out_elastic", progress -> {
		float base = Mth.TWO_PI / 3.0F;
		if (progress == 0.0F) {
			return 0.0F;
		} else if (progress == 1.0F) {
			return 1.0F;
		} else {
			return (float) (-Math.pow(2, -10 * progress) * Math.sin((progress * 10 - 0.75F) * base) + 1);
		}
	});
	public static final Function<Float, Float> EASE_IN_OUT_ELASTIC = register("ease_in_out_elastic", progress -> {
		float base = Mth.TWO_PI / 4.5F;
		if (progress == 0.0F) {
			return 0.0F;
		} else if (progress == 1.0F) {
			return 1.0F;
		} else if (progress < 0.5F) {
			return (float) (-(Math.pow(2, 20 * progress - 10) * Math.sin((20 * progress - 11.125F) * base)) * 0.5F);
		} else  {
			return (float) ((Math.pow(2, -20 * progress + 10) * Math.sin((20 * progress - 11.125F) * base)) * 0.5F + 1);
		}
	});
	public static final Function<Float, Float> EASE_IN_BOUNCE = register("ease_in_bounce", progress -> 1.0F - bounce(1.0F - progress));
	public static final Function<Float, Float> EASE_OUT_BOUNCE = register("ease_out_bounce", EndimationEasers::bounce);
	public static final Function<Float, Float> EASE_IN_OUT_BOUNCE = register("ease_in_out_bounce", progress -> {
		if (progress < 0.5F) {
			return  (1.0F - bounce(1 - 2 * progress)) * 0.5F;
		} else {
			return (1.0F + bounce(2 * progress - 1)) * 0.5F;
		}
	});

	private static float bounce(float progress) {
		float base1 = 7.5625F;
		float base2 = 2.75F;
		if (progress < 1.0F / base2) {
			return base1 * progress * progress;
		} else if (progress < 2.0F / base2) {
			return base1 * (progress -= 1.5F / base2) * progress + 0.75F;
		} else if (progress < 2.5F / base2) {
			return base1 * (progress -= 2.25F / base2) * progress + 0.9375F;
		} else {
			return base1 * (progress -= 2.625F / base2) * progress + 0.984375F;
		}
	}

	private EndimationEasers() {}

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
