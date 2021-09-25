package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AbnormalsSignTileEntity extends SignBlockEntity {

	public AbnormalsSignTileEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return ACTileEntities.SIGN.get();
	}

}