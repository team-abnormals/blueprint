package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class AbnormalsBeehiveTileEntity extends BeehiveBlockEntity {
	public AbnormalsBeehiveTileEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Nonnull
	@Override
	public BlockEntityType<?> getType() {
		return ACTileEntities.BEEHIVE.get();
	}
}
