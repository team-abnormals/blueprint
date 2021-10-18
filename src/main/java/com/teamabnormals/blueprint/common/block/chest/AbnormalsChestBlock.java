package com.teamabnormals.blueprint.common.block.chest;

import com.teamabnormals.blueprint.common.block.entity.AbnormalsChestBlockEntity;
import com.teamabnormals.blueprint.core.api.IChestBlock;
import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link ChestBlock} extension used for Blueprint's chests.
 */
public class AbnormalsChestBlock extends ChestBlock implements IChestBlock {
	public final String type;

	public AbnormalsChestBlock(String type, Properties props) {
		super(props, BlueprintBlockEntityTypes.CHEST::get);
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
