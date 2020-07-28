package com.minecraftabnormals.abnormals_core.common.tileentity;

import javax.annotation.Nonnull;

import com.minecraftabnormals.abnormals_core.core.examples.ExampleTileEntityRegistry;

import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class AbnormalsBeehiveTileEntity extends BeehiveTileEntity {	
	
	public AbnormalsBeehiveTileEntity() {
        super();
    }
	
	@Nonnull
    @Override
    public TileEntityType<?> getType() {
        return ExampleTileEntityRegistry.BEEHIVE.get();
    }
}
