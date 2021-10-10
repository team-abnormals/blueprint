package com.minecraftabnormals.abnormals_core.common.blockentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class AbnormalsBeehiveBlockEntity extends BeehiveBlockEntity {

	public AbnormalsBeehiveBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Nonnull
	@Override
	public BlockEntityType<?> getType() {
		return ACBlockEntities.BEEHIVE.get();
	}

}
