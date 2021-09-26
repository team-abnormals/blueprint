package com.minecraftabnormals.abnormals_core.common.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BELWRBlockItem extends BlockItem {
	private final Supplier<LazyBELWR> belwr;

	public BELWRBlockItem(Block block, Properties properties, Supplier<LazyBELWR> belwr) {
		super(block, properties);
		this.belwr = belwr;
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		LazyBELWR belwr = this.belwr.get();
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return belwr.get(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
			}
		});
	}

	public static final class LazyBELWR {
		private final BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> function;
		private BlockEntityWithoutLevelRenderer value;

		public LazyBELWR(BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> function) {
			this.function = function;
		}

		public BlockEntityWithoutLevelRenderer get(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
			BlockEntityWithoutLevelRenderer value = this.value;
			if (value == null) {
				value = this.value = this.function.apply(dispatcher, modelSet);
			}
			return value;
		}
	}
}
