package com.teamabnormals.blueprint.common.block.entity;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link BlueprintChestBlockEntity} extension used for Blueprint's trapped chests.
 */
public class BlueprintTrappedChestBlockEntity extends BlueprintChestBlockEntity {

	public BlueprintTrappedChestBlockEntity(BlockPos pos, BlockState state) {
		super(BlueprintBlockEntityTypes.TRAPPED_CHEST.get(), pos, state);
	}

	@Override
	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int oldOpenCount, int openCount) {
		super.signalOpenCount(level, pos, state, oldOpenCount, openCount);
		if (oldOpenCount != openCount) {
			Block block = state.getBlock();
			level.updateNeighborsAt(pos, block);
			level.updateNeighborsAt(pos.below(), block);
		}
	}
}