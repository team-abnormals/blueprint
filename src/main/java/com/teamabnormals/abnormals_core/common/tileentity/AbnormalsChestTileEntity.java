package com.teamabnormals.abnormals_core.common.tileentity;

import com.teamabnormals.abnormals_core.core.registry.ACTileEntities;

import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;

public class AbnormalsChestTileEntity extends ChestTileEntity {
	protected AbnormalsChestTileEntity(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public AbnormalsChestTileEntity() {
		super(ACTileEntities.CHEST.get());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
	}
}