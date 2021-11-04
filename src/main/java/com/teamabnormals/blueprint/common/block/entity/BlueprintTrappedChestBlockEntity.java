package com.teamabnormals.blueprint.common.block.entity;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An {@link BlueprintChestBlockEntity} extension used for Blueprint's trapped chests.
 */
public class BlueprintTrappedChestBlockEntity extends BlueprintChestBlockEntity {

	public BlueprintTrappedChestBlockEntity(BlockPos pos, BlockState state) {
		super(BlueprintBlockEntityTypes.TRAPPED_CHEST.get(), pos, state);
	}

	@Override
	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int int1, int int2) {
		super.signalOpenCount(level, pos, state, int1, int2);
		level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
	}

}