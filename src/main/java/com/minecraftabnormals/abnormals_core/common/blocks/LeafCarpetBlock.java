package com.minecraftabnormals.abnormals_core.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nonnull;

public class LeafCarpetBlock extends Block {
	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

	public LeafCarpetBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
}
