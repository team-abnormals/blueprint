package com.teamabnormals.blueprint.common.block.thatch;

import com.teamabnormals.blueprint.common.block.quark.VerticalSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A {@link VerticalSlabBlock} extension with certain methods overridden to accommodate models for thatch-type vertical slabs.
 */
@SuppressWarnings("deprecation")
public class ThatchVerticalSlabBlock extends VerticalSlabBlock {

	public ThatchVerticalSlabBlock(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

}