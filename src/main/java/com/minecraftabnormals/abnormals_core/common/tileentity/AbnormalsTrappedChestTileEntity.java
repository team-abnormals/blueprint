package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AbnormalsTrappedChestTileEntity extends AbnormalsChestTileEntity {
	public AbnormalsTrappedChestTileEntity(BlockPos pos, BlockState state) {
		super(ACTileEntities.TRAPPED_CHEST.get(), pos, state);
	}

	@Override
	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int int1, int int2) {
		super.signalOpenCount(level, pos, state, int1, int2);
		level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
	}
}