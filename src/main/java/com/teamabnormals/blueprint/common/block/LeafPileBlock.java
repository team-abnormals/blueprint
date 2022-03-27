package com.teamabnormals.blueprint.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Random;
import java.util.stream.Stream;

public class LeafPileBlock extends MultifaceBlock implements BonemealableBlock, SimpleWaterloggedBlock {
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public LeafPileBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, facingState, level, pos, facingPos);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return !context.getItemInHand().is(this.asItem()) || super.canBeReplaced(state, context);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClientSide) {
		return Stream.of(DIRECTIONS).anyMatch((direction) -> this.canSpread(state, level, pos, direction.getOpposite()));
	}

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
		this.spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
	}
}
