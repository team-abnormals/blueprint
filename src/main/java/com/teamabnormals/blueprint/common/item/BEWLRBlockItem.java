package com.teamabnormals.blueprint.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link BlockItem} extension that supports lazily loaded custom item stack renderers.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Deprecated(forRemoval = true)
// TODO: Examine during server testing
public class BEWLRBlockItem extends BlockItem {
	private final Supplier<BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer>> bewlrFactory;

	public BEWLRBlockItem(Block block, Properties properties, Supplier<BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer>> bewlrFactory) {
		super(block, properties);
		this.bewlrFactory = FMLEnvironment.dist.isClient() ? bewlrFactory : null;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			BlockEntityWithoutLevelRenderer bewlr = null;

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return bewlr != null ? bewlr : (bewlr = bewlrFactory.get().apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));
			}
		});
	}
}
