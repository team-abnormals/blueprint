package com.teamabnormals.blueprint.common.block.thatch;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link Block} extension with certain methods overridden to accommodate models for thatch-type blocks.
 */
public class ThatchBlock extends Block {

	public ThatchBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

}