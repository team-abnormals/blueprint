package com.minecraftabnormals.abnormals_core.common.world.gen;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

import java.util.function.LongFunction;

/**
 * Utility class for creating layers.
 * <p>Currently only used for creating the end biome layer.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ACLayerUtil {
	/**
	 * Creates a {@link Layer} containing end biomes from the AC end biome registry.
	 *
	 * @param lookupRegistry A {@link Registry} to lookup the biomes from.
	 * @param contextFactory A {@link LongFunction} to use as a factory for the context.
	 * @param <R>            The type of {@link IExtendedNoiseRandom} the factory creates.
	 * @return A {@link Layer} containing randomized end biomes from the AC end biome registry.
	 */
	public static <R extends IExtendedNoiseRandom<LazyArea>> Layer createEndBiomeLayer(Registry<Biome> lookupRegistry, LongFunction<R> contextFactory) {
		IAreaFactory<LazyArea> biomesFactory = new EndBiomesLayer(lookupRegistry).run(contextFactory.apply(1L));
		biomesFactory = LayerUtil.zoom(100L, ZoomLayer.NORMAL, biomesFactory, 2, contextFactory);

		for (int i = 0; i < 3; i++) {
			biomesFactory = ZoomLayer.NORMAL.run(contextFactory.apply(1000L + (long) i), biomesFactory);
		}

		biomesFactory = SmoothLayer.INSTANCE.run(contextFactory.apply(100L), biomesFactory);
		return new Layer(VoroniZoomLayer.INSTANCE.run(contextFactory.apply(10L), biomesFactory));
	}

	public enum VoroniZoomLayer implements IAreaTransformer1 {
		INSTANCE;

		public int applyPixel(IExtendedNoiseRandom<?> extendedNoiseRandom, IArea area, int p_215728_3_, int p_215728_4_) {
			int i = p_215728_3_ - 2;
			int j = p_215728_4_ - 2;
			int k = i >> 2;
			int l = j >> 2;
			int i1 = k << 2;
			int j1 = l << 2;
			extendedNoiseRandom.initRandom(i1, j1);
			double d0 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
			double d1 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
			extendedNoiseRandom.initRandom(i1 + 4, j1);
			double d2 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
			double d3 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
			extendedNoiseRandom.initRandom(i1, j1 + 4);
			double d4 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
			double d5 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
			extendedNoiseRandom.initRandom(i1 + 4, j1 + 4);
			double d6 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
			double d7 = ((double) extendedNoiseRandom.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
			int k1 = i & 3;
			int l1 = j & 3;
			double d8 = ((double) l1 - d1) * ((double) l1 - d1) + ((double) k1 - d0) * ((double) k1 - d0);
			double d9 = ((double) l1 - d3) * ((double) l1 - d3) + ((double) k1 - d2) * ((double) k1 - d2);
			double d10 = ((double) l1 - d5) * ((double) l1 - d5) + ((double) k1 - d4) * ((double) k1 - d4);
			double d11 = ((double) l1 - d7) * ((double) l1 - d7) + ((double) k1 - d6) * ((double) k1 - d6);
			if (d8 < d9 && d8 < d10 && d8 < d11) {
				return area.get(this.getParentX(i1), this.getParentY(j1));
			} else if (d9 < d8 && d9 < d10 && d9 < d11) {
				return area.get(this.getParentX(i1 + 4), this.getParentY(j1)) & 255;
			} else {
				return d10 < d8 && d10 < d9 && d10 < d11 ? area.get(this.getParentX(i1), this.getParentY(j1 + 4)) : area.get(this.getParentX(i1 + 4), this.getParentY(j1 + 4)) & 255;
			}
		}

		@Override
		public int getParentX(int x) {
			return x >> 2;
		}

		@Override
		public int getParentY(int z) {
			return z >> 2;
		}
	}

	static class EndBiomesLayer implements IAreaTransformer0 {
		private final Registry<Biome> lookupRegistry;

		EndBiomesLayer(Registry<Biome> lookupRegistry) {
			this.lookupRegistry = lookupRegistry;
		}

		@Override
		public int applyPixel(INoiseRandom random, int x, int z) {
			return this.lookupRegistry.getId(this.lookupRegistry.get(BiomeUtil.getEndBiome(random)));
		}
	}
}
