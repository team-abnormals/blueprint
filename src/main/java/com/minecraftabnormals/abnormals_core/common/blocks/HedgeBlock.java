package com.minecraftabnormals.abnormals_core.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class HedgeBlock extends FenceBlock {
	private static final BooleanProperty EXTEND = BooleanProperty.create("extend");

	public HedgeBlock(Properties properties) {
		super(properties);
		
		this.setDefaultState(this.getDefaultState().with(EXTEND, false));
	}
	
	@Override
	public boolean canConnect(BlockState state, boolean isSideSolid, Direction direction) {
		return state.getBlock() instanceof HedgeBlock;
	}
	
	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
		return facing == Direction.UP && !state.get(WATERLOGGED) && plantable.getPlantType(world, pos) == PlantType.PLAINS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(EXTEND, context.getWorld().getBlockState(context.getPos().down()).getBlock() instanceof HedgeBlock);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		if(facing == Direction.DOWN)
			return stateIn.with(EXTEND, facingState.getBlock() instanceof HedgeBlock);
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(EXTEND);
	}

}
