package com.minecraftabnormals.abnormals_core.common.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BEWLRBlockItem extends BlockItem {
	@Nullable
	private final Supplier<LazyBEWLR> bewlr;

	public BEWLRBlockItem(Block block, Properties properties, Supplier<Callable<LazyBEWLR>> bewlr) {
		super(block, properties);
		LazyBEWLR lazyBEWLR = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, bewlr);
		this.bewlr = lazyBEWLR == null ? null : () -> lazyBEWLR;
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				LazyBEWLR lazyBEWLR = bewlr.get();
				BlockEntityWithoutLevelRenderer value = lazyBEWLR.value;
				if (value != null) {
					return value;
				}
				return lazyBEWLR.cache(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
			}
		});
	}

	/**
	 * It's best if we don't have to create a new {@link BlockEntityRenderDispatcher} instance every time the item is rendered.
	 */
	public static final class LazyBEWLR {
		private final BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> cacheFunction;
		public BlockEntityWithoutLevelRenderer value;

		public LazyBEWLR(BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> cacheFunction) {
			this.cacheFunction = cacheFunction;
		}

		public BlockEntityWithoutLevelRenderer cache(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
			return this.value = this.cacheFunction.apply(dispatcher, modelSet);
		}
	}
}
