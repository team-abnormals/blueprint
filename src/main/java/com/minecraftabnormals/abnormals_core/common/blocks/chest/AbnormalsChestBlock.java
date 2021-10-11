package com.minecraftabnormals.abnormals_core.common.blocks.chest;

import com.minecraftabnormals.abnormals_core.common.blockentity.AbnormalsChestBlockEntity;
import com.minecraftabnormals.abnormals_core.core.api.IChestBlock;
import com.minecraftabnormals.abnormals_core.core.registry.ACBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link ChestBlock} extension used for Abnormals Core's chests.
 */
public class AbnormalsChestBlock extends ChestBlock implements IChestBlock {
	public final String type;

	public AbnormalsChestBlock(String type, Properties props) {
		super(props, ACBlockEntities.CHEST::get);
		this.type = type;
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AbnormalsChestBlockEntity(pos, state);
	}

	@Override
	public String getChestType() {
		return type;
	}
}
