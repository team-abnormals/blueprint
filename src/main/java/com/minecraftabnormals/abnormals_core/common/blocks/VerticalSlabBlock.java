package com.minecraftabnormals.abnormals_core.common.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

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
		return state.get(TYPE).shape;
	}
	
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		BlockState blockstate = context.getWorld().getBlockState(blockpos);
		if(blockstate.getBlock() == this) 
			return blockstate.with(TYPE, VerticalSlabType.DOUBLE).with(WATERLOGGED, false);
		
		FluidState fluid = context.getWorld().getFluidState(blockpos);
		BlockState retState = getDefaultState().with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
		Direction direction = getDirectionForPlacement(context);
		VerticalSlabType type = VerticalSlabType.fromDirection(direction);
		
		return retState.with(TYPE, type);
	}
	
	private Direction getDirectionForPlacement(BlockItemUseContext context) {
		Direction direction = context.getFace();
		if(direction.getAxis() != Axis.Y)
			return direction;
		
		BlockPos pos = context.getPos();
		Vector3d vec = context.getHitVec().subtract(new Vector3d(pos.getX(), pos.getY(), pos.getZ())).subtract(0.5, 0, 0.5);
		double angle = Math.atan2(vec.x, vec.z) * -180.0 / Math.PI;
		return Direction.fromAngle(angle).getOpposite();
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
	public boolean isReplaceable(BlockState state, @Nonnull BlockItemUseContext useContext) {
		ItemStack itemstack = useContext.getItem();
		VerticalSlabType slabtype = state.get(TYPE);
		return slabtype != VerticalSlabType.DOUBLE && itemstack.getItem() == this.asItem()  &&
			(useContext.replacingClickedOnBlock() && (useContext.getFace() == slabtype.direction && getDirectionForPlacement(useContext) == slabtype.direction)
					|| (!useContext.replacingClickedOnBlock() && useContext.getFace().getAxis() != slabtype.direction.getAxis()));
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
		
		private final String name;
        public final Direction direction;
        public final VoxelShape shape;

        VerticalSlabType(Direction directionIn) {
            direction = directionIn;
            name = direction == null ? "double" : direction.getString();
            if (direction == null)
                shape = VoxelShapes.fullCube();
            else {
                double min = 0;
                double max = 8;
                if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                    min = 8;
                    max = 16;
                }
                if (direction.getAxis() == Direction.Axis.X) {
                    shape = Block.makeCuboidShape(min, 0, 0, max, 16, 16);
                } else shape = Block.makeCuboidShape(0, 0, min, 16, 16, max);
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
            return name;
        }

        @Nonnull
        @Override
        public String getString() {
            return name;
        }
	}
}