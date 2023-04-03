package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.common.entity.BlueprintFallingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link FallingBlock} extension with some additional methods.
 * The block turns into a {@link BlueprintFallingBlockEntity} when it falls.
 */
public class BlueprintFallingBlock extends FallingBlock {

	public BlueprintFallingBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
			BlueprintFallingBlockEntity fallingblockentity = BlueprintFallingBlockEntity.fall(level, pos, state);
			fallingblockentity.setDropsBlockLoot(this.dropsBlockLoot(fallingblockentity, state, pos));
			fallingblockentity.setAllowsPlacing(this.allowsPlacing(fallingblockentity, state, pos));
			this.falling(fallingblockentity);
		}
	}

	/**
	 * If true, the falling block will use the loot table of its block state to determine its drops when broken by a fall.
	 */
	public boolean dropsBlockLoot(BlueprintFallingBlockEntity fallingBlockEntity, BlockState state, BlockPos pos) {
		return true;
	}

	/**
	 * If true, the falling block will not place itself when it hits the ground. Instead, it will break into its drops.
	 */
	public boolean allowsPlacing(BlueprintFallingBlockEntity fallingBlockEntity, BlockState state, BlockPos pos) {
		return true;
	}
}