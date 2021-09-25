package com.minecraftabnormals.abnormals_core.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class HedgeBlock extends FenceBlock {
	public static final Tag<Block> HEDGES = BlockTags.createOptional(new ResourceLocation("quark", "hedges"));
	private static final BooleanProperty EXTEND = BooleanProperty.create("extend");

	public HedgeBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(EXTEND, false));
	}
	
	@Override
	public boolean connectsTo(BlockState state, boolean isSideSolid, Direction direction) {
		return state.getBlock().is(HEDGES);
	}
	
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return facing == Direction.UP && !state.getValue(WATERLOGGED) && plantable.getPlantType(world, pos) == PlantType.PLAINS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(EXTEND, context.getLevel().getBlockState(context.getClickedPos().below()).getBlock().is(HEDGES));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return facing == Direction.DOWN ? stateIn.setValue(EXTEND, facingState.getBlock().is(HEDGES)) : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(EXTEND);
	}
}
