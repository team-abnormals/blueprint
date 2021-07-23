package com.minecraftabnormals.abnormals_core.common.blocks.chest;

import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsChestTileEntity;
import com.minecraftabnormals.abnormals_core.core.api.IChestBlock;
import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.AbstractBlock.Properties;

public class AbnormalsChestBlock extends ChestBlock implements IChestBlock {
	public final String type;

	public AbnormalsChestBlock(String type, Properties props) {
		super(props, () -> ACTileEntities.CHEST.get());
		this.type = type;
	}

	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new AbnormalsChestTileEntity();
	}

	@Override
	public String getChestType() {
		return type;
	}
}
