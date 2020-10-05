package com.teamabnormals.abnormals_core.common.tileentity;

import com.teamabnormals.abnormals_core.core.examples.ExampleTileEntityRegistry;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class AbnormalsSignTileEntity extends SignTileEntity {

	@Override
	public TileEntityType<?> getType() {
		return ExampleTileEntityRegistry.SIGN.get();
	}
}