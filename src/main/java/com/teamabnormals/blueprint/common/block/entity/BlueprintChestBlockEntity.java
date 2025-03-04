package com.teamabnormals.blueprint.common.block.entity;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link ChestBlockEntity} extension used for Blueprint's chests.
 */
public class BlueprintChestBlockEntity extends ChestBlockEntity {

	protected BlueprintChestBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	public BlueprintChestBlockEntity(BlockPos pos, BlockState state) {
		super(BlueprintBlockEntityTypes.CHEST.get(), pos, state);
	}

}