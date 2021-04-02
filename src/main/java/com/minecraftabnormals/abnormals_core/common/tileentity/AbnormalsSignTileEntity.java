package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class AbnormalsSignTileEntity extends SignTileEntity {

	@Override
	public TileEntityType<?> getType() {
		return ACTileEntities.SIGN.get();
	}

}