package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;

public class AbnormalsTrappedChestTileEntity extends AbnormalsChestTileEntity {
	public AbnormalsTrappedChestTileEntity() {
		super(ACTileEntities.TRAPPED_CHEST.get());
	}

	protected void onOpenOrClose() {
		super.onOpenOrClose();
		this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockState().getBlock());
	}
}