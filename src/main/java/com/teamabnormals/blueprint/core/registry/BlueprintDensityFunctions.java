package com.teamabnormals.blueprint.core.registry;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The class for Blueprint's density function types.
 * <p>Internal-use types are not documented.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintDensityFunctions {
	public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, Blueprint.MOD_ID);

	public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<? extends DensityFunction>> SINGLE_POINT_CACHE = DENSITY_FUNCTION_TYPES.register("single_point_cache", SinglePointCacheDensityFunction.CODEC::codec);

	public static class SinglePointCacheDensityFunction implements DensityFunction {
		public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(DensityFunction.HOLDER_HELPER_CODEC.xmap(function -> function, function -> function instanceof SinglePointCacheDensityFunction scopedCacheDensityFunction ? scopedCacheDensityFunction.base : function).fieldOf("base"));
		private final DensityFunction base;
		private final FunctionContext scopedContext;
		private volatile Double singlePointValue;

		public SinglePointCacheDensityFunction(DensityFunction base, FunctionContext scopedContext) {
			this.base = base;
			this.scopedContext = scopedContext;
		}

		public DensityFunction base() {
			return this.base;
		}

		public void reset() {
			this.singlePointValue = null;
		}

		@Override
		public double compute(FunctionContext context) {
			FunctionContext scopedContext = this.scopedContext;
			if (scopedContext.blockX() != context.blockX() || scopedContext.blockY() != context.blockY() || scopedContext.blockZ() != context.blockZ())
				return this.base.compute(context);
			Double singlePointValue = this.singlePointValue;
			if (singlePointValue == null) {
				// It's very unlikely that this thread-safety is needed
				// In vanilla it's not, but other mods could parallelize during biome selection
				synchronized (this) {
					singlePointValue = this.singlePointValue;
					if (singlePointValue == null)
						return this.singlePointValue = this.base.compute(context);
				}
			}
			return singlePointValue;
		}

		@Override
		public void fillArray(double[] array, ContextProvider contextProvider) {
			this.base.fillArray(array, contextProvider);
		}

		@Override
		public DensityFunction mapAll(Visitor visitor) {
			return this.base.mapAll(visitor);
		}

		@Override
		public double minValue() {
			return this.base.minValue();
		}

		@Override
		public double maxValue() {
			return this.base.maxValue();
		}

		@Override
		public KeyDispatchDataCodec<? extends DensityFunction> codec() {
			return CODEC;
		}
	}
}
