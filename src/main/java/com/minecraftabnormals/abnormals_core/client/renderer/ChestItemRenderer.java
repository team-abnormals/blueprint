package com.minecraftabnormals.abnormals_core.client.renderer;

import com.minecraftabnormals.abnormals_core.client.tile.AbnormalsChestTileEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ChestItemRenderer<T extends BlockEntity> extends BlockEntityWithoutLevelRenderer {
	private final Supplier<T> te;

	public ChestItemRenderer(Supplier<T> te) {
		this.te = te;
	}

	@Override
	public void renderByItem(ItemStack itemStackIn, TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		BlockItem blockItem = (BlockItem) itemStackIn.getItem();
		AbnormalsChestTileEntityRenderer.itemBlock = blockItem.getBlock();
		BlockEntityRenderDispatcher.instance.renderItem(this.te.get(), matrixStack, buffer, combinedLight, combinedOverlay);
		AbnormalsChestTileEntityRenderer.itemBlock = null;
	}
}
