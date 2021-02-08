package com.minecraftabnormals.abnormals_core.common.blocks.wood;

import java.util.function.Supplier;

import com.minecraftabnormals.abnormals_core.core.util.BlockUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.LanternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class WoodPostBlock extends Block implements IWaterLoggable {
	private static final VoxelShape SHAPE_X = makeCuboidShape(0.0F, 6.0F, 6.0F, 16.0F, 10.0F, 10.0F);
	private static final VoxelShape SHAPE_Y = makeCuboidShape(6.0F, 0.0F, 6.0F, 10.0F, 16.0F, 10.0F);
	private static final VoxelShape SHAPE_Z = makeCuboidShape(6.0F, 6.0F, 0.0F, 10.0F, 10.0F, 16.0F);
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
	private static final BooleanProperty[] CHAINED = new BooleanProperty[] {
			BooleanProperty.create("chain_down"),
			BooleanProperty.create("chain_up"),
			BooleanProperty.create("chain_north"),
			BooleanProperty.create("chain_south"),
			BooleanProperty.create("chain_west"),
			BooleanProperty.create("chain_east")
	};
	
	private final Supplier<Block> block;
	
	public WoodPostBlock(Properties properties) {
		this(null, properties);
	}
	
	public WoodPostBlock(Supplier<Block> block, Properties properties) {
		super(properties);
		this.block = block;
		BlockState defaultState = stateContainer.getBaseState().with(WATERLOGGED, false).with(AXIS, Axis.Y);
		for (BooleanProperty prop : CHAINED)
			defaultState = defaultState.with(prop, false);
		this.setDefaultState(defaultState);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(AXIS)) {
		case X: return SHAPE_X;
		case Y: return SHAPE_Y;
		default: return SHAPE_Z;
		}
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return !state.get(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getState(context.getWorld(), context.getPos(), context.getFace().getAxis());
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState newState = this.getState(worldIn, pos, state.get(AXIS));
		if (!newState.equals(state))
			worldIn.setBlockState(pos, newState);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, AXIS);
		for (BooleanProperty prop : CHAINED)
			builder.add(prop);
	}
	
	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		if (toolType == ToolType.AXE)
			return block != null ? BlockUtil.transferAllBlockStates(state, this.block.get().getDefaultState()) : null;
		return super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}

	private BlockState getState(World world, BlockPos pos, Axis axis) {
		BlockState state = this.getDefaultState().with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER).with(AXIS, axis);

		for (Direction direction : Direction.values()) {
			if (direction.getAxis() == axis)
				continue;

			BlockState sideState = world.getBlockState(pos.offset(direction));
			if ((sideState.getBlock() instanceof ChainBlock && sideState.get(BlockStateProperties.AXIS) == direction.getAxis()) 
					|| (direction == Direction.DOWN && sideState.getBlock() instanceof LanternBlock && sideState.get(LanternBlock.HANGING)))
				state = state.with(CHAINED[direction.ordinal()], true);
		}

		return state;
	}
}
