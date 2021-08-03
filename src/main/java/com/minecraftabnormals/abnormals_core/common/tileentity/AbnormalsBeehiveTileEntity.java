package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class AbnormalsBeehiveTileEntity extends BeehiveTileEntity {
	public AbnormalsBeehiveTileEntity() {
		super();
	}

	@Nonnull
	@Override
	public TileEntityType<?> getType() {
		return ACTileEntities.BEEHIVE.get();
	}
}
