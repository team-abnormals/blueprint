package com.teamabnormals.blueprint.common.block.entity;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

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

	@Override
	public AABB getRenderBoundingBox() {
		BlockPos worldPos = this.worldPosition;
		int x = worldPos.getX();
		int y = worldPos.getY();
		int z = worldPos.getZ();
		return new AABB(x - 1, y, z - 1, x + 2, y + 2, z + 2);
	}

}