package com.teamabnormals.blueprint.common.block.entity;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

/**
 * A {@link HangingSignBlockEntity} extension used for Blueprint's hanging signs.
 */
public class BlueprintHangingSignBlockEntity extends HangingSignBlockEntity {
	public static final HashSet<Block> VALID_BLOCKS = new HashSet<>();

	public BlueprintHangingSignBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return BlueprintBlockEntityTypes.HANGING_SIGN.get();
	}
}
