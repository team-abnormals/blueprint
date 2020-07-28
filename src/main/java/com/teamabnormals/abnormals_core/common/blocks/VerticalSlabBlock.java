package com.teamabnormals.abnormals_core.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class VerticalSlabBlock extends Block implements IWaterLoggable {
	public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.create("type", VerticalSlabType.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public VerticalSlabBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(TYPE, VerticalSlabType.NORTH).with(WATERLOGGED, false));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED);
	}
	
	@Override
	public boolean isTransparent(BlockState state) {
		return state.get(TYPE) != VerticalSlabType.DOUBLE;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return state.get(TYPE).getShape();
	}
	
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos pos = context.getPos();
		World world = context.getWorld();
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == this) {
			return state.with(TYPE, VerticalSlabType.DOUBLE).with(WATERLOGGED, false);
		}
		return this.getDefaultState().with(TYPE, VerticalSlabType.getSlabTypeByDirection(this.calculateDirectionForPlacement(context))).with(WATERLOGGED, world.getFluidState(pos).getFluid().isIn(FluidTags.WATER));
	}
	
	protected Direction calculateDirectionForPlacement(BlockItemUseContext context) {
		Direction face = context.getFace();
		if(face.getAxis() != Direction.Axis.Y) {
			return face;
		}
		Vector3d difference = context.getHitVec().subtract(Vector3d.copyCentered(context.getPos())).subtract(0.5, 0, 0.5);
		return Direction.fromAngle(-Math.toDegrees(Math.atan2(difference.getX(), difference.getZ()))).getOpposite();
	}
	
	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext context) {
		VerticalSlabType slabtype = state.get(TYPE);
		return slabtype != VerticalSlabType.DOUBLE && context.getItem().getItem() == this.asItem() && context.replacingClickedOnBlock() && (context.getFace() == slabtype.slabDirection && this.calculateDirectionForPlacement(context) == slabtype.slabDirection);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
		return state.get(TYPE) != VerticalSlabType.DOUBLE && IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn);
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
		return state.get(TYPE) != VerticalSlabType.DOUBLE && IWaterLoggable.super.canContainFluid(worldIn, pos, state, fluidIn);
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if(state.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return state;
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return type == PathType.WATER && worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
	}

	public static enum VerticalSlabType implements IStringSerializable {
		NORTH(Direction.NORTH),
		SOUTH(Direction.SOUTH),
		WEST(Direction.WEST),
		EAST(Direction.EAST),
		DOUBLE(null);
		
		@Nullable
		public final Direction slabDirection;
		
		VerticalSlabType(@Nullable Direction slabDirection) {
			this.slabDirection = slabDirection;
		}
		
		public VoxelShape getShape() {
			if(this.slabDirection != null) {
				double minXZ = this.slabDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 8 : 0;
				double maxXZ = this.slabDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 16 : 8;
				return this.slabDirection.getAxis() == Direction.Axis.X ? Block.makeCuboidShape(minXZ, 0, 0, maxXZ, 16, 16) : Block.makeCuboidShape(0, 0, minXZ, 16, 16, maxXZ);
			}
			return VoxelShapes.fullCube();
		}
		
		@Nullable
		public static VerticalSlabType getSlabTypeByDirection(@Nullable Direction direction) {
			for(VerticalSlabType types : VerticalSlabType.values()) {
				if(types.slabDirection == direction) {
					return types;
				}
			}
			return null;
		}
		
		@Override
		public String toString() {
			return this.slabDirection != null ? this.slabDirection.getString() : "double";
		}

		@Override
		public String getString() {
			return this.toString();
		}
	}
}