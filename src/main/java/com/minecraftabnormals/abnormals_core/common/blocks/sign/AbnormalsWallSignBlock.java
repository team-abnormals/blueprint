package com.minecraftabnormals.abnormals_core.common.blocks.sign;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.AbstractBlock.Properties;

public class AbnormalsWallSignBlock extends WallSignBlock implements IAbnormalsSign {

	public AbnormalsWallSignBlock(Properties properties, WoodType woodType) {
		super(properties, woodType);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ACTileEntities.SIGN.get().create();
	}

}