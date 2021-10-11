package com.minecraftabnormals.abnormals_core.common.blockentity;

import com.minecraftabnormals.abnormals_core.core.registry.ACBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link SignBlockEntity} extension used for Abnormals Core's signs.
 */
public class AbnormalsSignBlockEntity extends SignBlockEntity {

	public AbnormalsSignBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return ACBlockEntities.SIGN.get();
	}

}