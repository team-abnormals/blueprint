package com.teamabnormals.abnormals_core.common.blocks.chest;

import com.google.common.base.Supplier;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsChestTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class AbnormalsChestBlock extends ChestBlock {

	public final String type;
	
	public AbnormalsChestBlock(String type, Supplier<TileEntityType<? extends ChestTileEntity>> supplier, Properties props) {
		super(props, supplier);		
		this.type = type;
	}
	
	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new AbnormalsChestTileEntity();
	}

	public String getChestName() {
		return type;
	}
}
