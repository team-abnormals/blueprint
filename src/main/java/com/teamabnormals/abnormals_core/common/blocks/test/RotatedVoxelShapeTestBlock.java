package com.teamabnormals.abnormals_core.common.blocks.test;

import com.teamabnormals.abnormals_core.core.library.Test;
import com.teamabnormals.abnormals_core.core.utils.BlockUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

@Test
public class RotatedVoxelShapeTestBlock extends HorizontalBlock {

	public RotatedVoxelShapeTestBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = makeCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
		return VoxelShapes.create(BlockUtils.rotateHorizontalBB(shape.getBoundingBox(), BlockUtils.BBRotation.getRotationForDirection(state.get(HORIZONTAL_FACING), Direction.NORTH)));
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

}