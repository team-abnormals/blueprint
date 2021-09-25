package com.minecraftabnormals.abnormals_core.common.tileentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AbnormalsChestTileEntity extends ChestBlockEntity {
	protected AbnormalsChestTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	public AbnormalsChestTileEntity(BlockPos pos, BlockState state) {
		super(ACTileEntities.CHEST.get(), pos, state);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1, worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2);
	}
}