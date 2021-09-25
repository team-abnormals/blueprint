package com.minecraftabnormals.abnormals_core.common.blocks.chest;

import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsChestTileEntity;
import com.minecraftabnormals.abnormals_core.core.api.IChestBlock;
import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AbnormalsChestBlock extends ChestBlock implements IChestBlock {
	public final String type;

	public AbnormalsChestBlock(String type, Properties props) {
		super(props, () -> ACTileEntities.CHEST.get());
		this.type = type;
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn) {
		return new AbnormalsChestTileEntity();
	}

	@Override
	public String getChestType() {
		return type;
	}
}
