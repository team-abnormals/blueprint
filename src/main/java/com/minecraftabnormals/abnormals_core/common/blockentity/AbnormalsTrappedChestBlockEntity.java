package com.minecraftabnormals.abnormals_core.common.blockentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An {@link AbnormalsChestBlockEntity} extension used for Abnormals Core's trapped chests.
 */
public class AbnormalsTrappedChestBlockEntity extends AbnormalsChestBlockEntity {

	public AbnormalsTrappedChestBlockEntity(BlockPos pos, BlockState state) {
		super(ACBlockEntities.TRAPPED_CHEST.get(), pos, state);
	}

	@Override
	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int int1, int int2) {
		super.signalOpenCount(level, pos, state, int1, int2);
		level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
	}

}