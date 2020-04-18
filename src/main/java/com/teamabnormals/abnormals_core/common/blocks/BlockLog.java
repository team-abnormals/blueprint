package com.teamabnormals.abnormals_core.common.blocks;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BlockLog extends RotatedPillarBlock {
	Supplier<Block> block;

	public BlockLog(Supplier<Block> strippedBlock, Properties properties) {
		super(properties);
		this.block = strippedBlock;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if(player.getHeldItem(hand).getItem() instanceof AxeItem) {
			world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 2.0F, 1.0F);
			world.setBlockState(pos, block.get().getDefaultState().with(AXIS, state.get(AXIS)));
			return ActionResultType.SUCCESS;
		}
		return super.onBlockActivated(state, world, pos, player, hand, result);
	}

}
