package com.teamabnormals.blueprint.common.item;

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

/**
 * A {@link BlockItem} extension that supports lazily loaded custom item stack renderers.
 *
 * @author SmellyModder (Luke Tonon)
 * @see BEWLRBlockItem.LazyBEWLR
 */
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
	 * A caching class for custom {@link BlockEntityWithoutLevelRenderer}s used on items.
	 * <p>Without caching, a new {@link BlockEntityRenderDispatcher} instance would have to get created every time the item is rendered.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
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
