package com.teamabnormals.abnormals_core.client.renderer;

import com.google.common.base.Supplier;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamabnormals.abnormals_core.client.tile.AbnormalsChestTileEntityRenderer;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestItemRenderer<T extends TileEntity> extends ItemStackTileEntityRenderer {
	private final Supplier<T> te;

	public ChestItemRenderer(Supplier<T> te) {
		this.te = te;
	}

	@Override
	public void render(ItemStack itemStackIn, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		BlockItem blockItem = (BlockItem) itemStackIn.getItem();
		AbnormalsChestTileEntityRenderer.itemBlock = blockItem.getBlock();
		TileEntityRendererDispatcher.instance.renderItem(this.te.get(), matrixStack, buffer, combinedLight, combinedOverlay);
		AbnormalsChestTileEntityRenderer.itemBlock = null;
	}
}
