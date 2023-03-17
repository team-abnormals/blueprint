package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.common.entity.BlueprintFallingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link FallingBlock} extension that turns into a {@link BlueprintFallingBlockEntity} when it falls.
 */
public class BlueprintFallingBlock extends FallingBlock {

	public BlueprintFallingBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
			BlueprintFallingBlockEntity fallingblockentity = BlueprintFallingBlockEntity.fall(level, pos, state);
			this.falling(fallingblockentity);
		}
	}
}