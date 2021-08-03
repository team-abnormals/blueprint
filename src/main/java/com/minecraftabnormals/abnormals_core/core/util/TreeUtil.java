package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.block.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

/**
 * @author - bageldotjpg
 */
public final class TreeUtil {

	public static void placeLogAt(IWorldWriter world, BlockPos pos, Random rand, BaseTreeFeatureConfig config) {
		setForcedState(world, pos, config.trunkProvider.getState(rand, pos));
	}

	public static void placeDirectionalLogAt(IWorldWriter world, BlockPos pos, Direction direction, Random rand, BaseTreeFeatureConfig config) {
		setForcedState(world, pos, config.trunkProvider.getState(rand, pos).setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
	}

	public static boolean isInTag(IWorldGenerationBaseReader world, BlockPos pos, INamedTag<Block> tag) {
		return world.isStateAtPosition(pos, (block) -> block.is(tag));
	}

	public static void placeLeafAt(IWorldGenerationReader world, BlockPos pos, Random rand, BaseTreeFeatureConfig config) {
		if (isAirOrLeaves(world, pos)) {
			setForcedState(world, pos, config.leavesProvider.getState(rand, pos).setValue(LeavesBlock.DISTANCE, 1));
		}
	}

	public static void setForcedState(IWorldWriter world, BlockPos pos, BlockState state) {
		world.setBlock(pos, state, 18);
	}

	@SuppressWarnings("deprecation")
	public static boolean isAir(IWorldGenerationBaseReader world, BlockPos pos) {
		if (!(world instanceof IBlockReader)) {
			return world.isStateAtPosition(pos, BlockState::isAir);
		} else {
			return world.isStateAtPosition(pos, state -> state.isAir((net.minecraft.world.IBlockReader) world, pos));
		}
	}

	public static boolean isLog(IWorldGenerationBaseReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> state.is(BlockTags.LOGS));
	}

	public static boolean isLeaves(IWorldGenerationBaseReader worldIn, BlockPos pos) {
		return worldIn.isStateAtPosition(pos, (state) -> state.is(BlockTags.LEAVES));
	}

	public static boolean isAirOrLeaves(IWorldGenerationBaseReader world, BlockPos pos) {
		if (world instanceof IWorldReader) {
			return world.isStateAtPosition(pos, state -> state.canBeReplacedByLeaves((IWorldReader) world, pos));
		}
		return world.isStateAtPosition(pos, (state) -> isAir(world, pos) || state.is(BlockTags.LEAVES));
	}

	public static void setDirtAt(IWorld world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND) {
			world.setBlock(pos, Blocks.DIRT.defaultBlockState(), 18);
		}
	}

	public static boolean isValidGround(IWorld world, BlockPos pos, SaplingBlock sapling) {
		return world.getBlockState(pos).canSustainPlant(world, pos, Direction.UP, (IPlantable) sapling);
	}

}