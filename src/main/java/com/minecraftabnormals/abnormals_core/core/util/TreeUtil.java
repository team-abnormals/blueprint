package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

/**
 * @author - bageldotjpg
 */
public final class TreeUtil {

	public static void placeLogAt(LevelWriter world, BlockPos pos, Random rand, TreeConfiguration config) {
		setForcedState(world, pos, config.trunkProvider.getState(rand, pos));
	}

	public static void placeDirectionalLogAt(LevelWriter world, BlockPos pos, Direction direction, Random rand, TreeConfiguration config) {
		setForcedState(world, pos, config.trunkProvider.getState(rand, pos).setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
	}

	public static boolean isInTag(LevelSimulatedReader world, BlockPos pos, Named<Block> tag) {
		return world.isStateAtPosition(pos, (block) -> block.is(tag));
	}

	public static void placeLeafAt(LevelSimulatedRW world, BlockPos pos, Random rand, TreeConfiguration config) {
		if (isAirOrLeaves(world, pos)) {
			setForcedState(world, pos, config.foliageProvider.getState(rand, pos).setValue(LeavesBlock.DISTANCE, 1));
		}
	}

	public static void setForcedState(LevelWriter world, BlockPos pos, BlockState state) {
		world.setBlock(pos, state, 18);
	}

	public static boolean isLog(LevelSimulatedReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> state.is(BlockTags.LOGS));
	}

	public static boolean isLeaves(LevelSimulatedReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> state.is(BlockTags.LEAVES));
	}

	public static boolean isAirOrLeaves(LevelSimulatedReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> state.isAir() || state.is(BlockTags.LEAVES));
	}

	public static void setDirtAt(LevelAccessor world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND) {
			world.setBlock(pos, Blocks.DIRT.defaultBlockState(), 18);
		}
	}

	public static boolean isValidGround(LevelAccessor world, BlockPos pos, SaplingBlock sapling) {
		return world.getBlockState(pos).canSustainPlant(world, pos, Direction.UP, (IPlantable) sapling);
	}

}