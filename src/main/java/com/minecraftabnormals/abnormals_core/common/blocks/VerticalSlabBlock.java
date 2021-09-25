package com.minecraftabnormals.abnormals_core.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock {
	public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.create("type", VerticalSlabType.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public VerticalSlabBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, VerticalSlabType.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return state.getValue(TYPE) != VerticalSlabType.DOUBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return state.getValue(TYPE).shape;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		BlockState blockstate = context.getLevel().getBlockState(blockpos);
		if (blockstate.getBlock() == this)
			return blockstate.setValue(TYPE, VerticalSlabType.DOUBLE).setValue(WATERLOGGED, false);
		return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(blockpos).getType() == Fluids.WATER).setValue(TYPE, VerticalSlabType.fromDirection(this.getDirectionForPlacement(context)));
	}

	private Direction getDirectionForPlacement(BlockPlaceContext context) {
		Direction face = context.getClickedFace();
		if (face.getAxis() != Direction.Axis.Y) return face;
		Vec3 difference = context.getClickLocation().subtract(Vec3.atLowerCornerOf(context.getClickedPos())).subtract(0.5, 0, 0.5);
		return Direction.fromYRot(-Math.toDegrees(Math.atan2(difference.x(), difference.z()))).getOpposite();
	}

	@Override
	public boolean canBeReplaced(BlockState state, @Nonnull BlockPlaceContext context) {
		VerticalSlabType slabtype = state.getValue(TYPE);
		return slabtype != VerticalSlabType.DOUBLE && context.getItemInHand().getItem() == this.asItem() && context.replacingClickedOnBlock() && (context.getClickedFace() == slabtype.direction && this.getDirectionForPlacement(context) == slabtype.direction);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
		return state.getValue(TYPE) != VerticalSlabType.DOUBLE && SimpleWaterloggedBlock.super.placeLiquid(worldIn, pos, state, fluidStateIn);
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
		return state.getValue(TYPE) != VerticalSlabType.DOUBLE && SimpleWaterloggedBlock.super.canPlaceLiquid(worldIn, pos, state, fluidIn);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		return state;
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return type == PathComputationType.WATER && worldIn.getFluidState(pos).is(FluidTags.WATER);
	}

	public static enum VerticalSlabType implements StringRepresentable {
		NORTH(Direction.NORTH),
		SOUTH(Direction.SOUTH),
		WEST(Direction.WEST),
		EAST(Direction.EAST),
		DOUBLE(null);

		private final String name;
		@Nullable
		public final Direction direction;
		public final VoxelShape shape;

		VerticalSlabType(@Nullable Direction direction) {
			this.direction = direction;
			this.name = direction == null ? "double" : direction.getSerializedName();
			if (direction == null) {
				this.shape = Shapes.block();
			} else {
				boolean isNegativeAxis = direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
				double min = isNegativeAxis ? 8 : 0;
				double max = isNegativeAxis ? 16 : 8;
				this.shape = direction.getAxis() == Direction.Axis.X ? Block.box(min, 0, 0, max, 16, 16) : Block.box(0, 0, min, 16, 16, max);
			}
		}

		public static VerticalSlabType fromDirection(Direction direction) {
			for (VerticalSlabType type : VerticalSlabType.values()) {
				if (type.direction != null && direction == type.direction) {
					return type;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}