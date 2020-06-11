package com.teamabnormals.abnormals_core.client.tile;

import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class AbnormalsTrappedChestTileEntityRenderer<T extends TileEntity & IChestLid> extends AbnormalsChestTileEntityRenderer<T> {

	public AbnormalsTrappedChestTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public Material getMaterial(T t, ChestType type) {
		Block inventoryBlock = invBlock;
		if(inventoryBlock == null)
			inventoryBlock = t.getBlockState().getBlock();
		
		AbnormalsTrappedChestBlock block = (AbnormalsTrappedChestBlock) inventoryBlock;
		
		switch(type) {
			case LEFT: return new Material(Atlases.CHEST_ATLAS, new ResourceLocation(AbnormalsCore.MODID, "entity/chest/" + block.getChestName() + "/trapped_left"));
			case RIGHT: return new Material(Atlases.CHEST_ATLAS, new ResourceLocation(AbnormalsCore.MODID, "entity/chest/" + block.getChestName() + "/trapped_right"));
			case SINGLE: default: return new Material(Atlases.CHEST_ATLAS, new ResourceLocation(AbnormalsCore.MODID, "entity/chest/" + block.getChestName() + "/trapped"));
		}
	}
}