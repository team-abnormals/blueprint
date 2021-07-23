package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;

public class AbnormalsTrappedChestTileEntity extends AbnormalsChestTileEntity {
	public AbnormalsTrappedChestTileEntity() {
		super(ACTileEntities.TRAPPED_CHEST.get());
	}

	protected void signalOpenCount() {
		super.signalOpenCount();
		this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
	}
}