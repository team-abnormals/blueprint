package com.minecraftabnormals.abnormals_core.client.renderer;

import com.minecraftabnormals.abnormals_core.client.tile.AbnormalsChestTileEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class ChestBlockEntityWithoutLevelRenderer<T extends BlockEntity> extends TypedBlockEntityWithoutLevelRenderer<T> {

	public ChestBlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, LazyLoadedValue<T> be) {
		super(dispatcher, modelSet, be);
	}

	@Override
	public void renderByItem(ItemStack itemStackIn, TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		BlockItem blockItem = (BlockItem) itemStackIn.getItem();
		AbnormalsChestTileEntityRenderer.itemBlock = blockItem.getBlock();
		super.renderByItem(itemStackIn, transformType, matrixStack, buffer, combinedLight, combinedOverlay);
		AbnormalsChestTileEntityRenderer.itemBlock = null;
	}

}
