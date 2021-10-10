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

import java.util.Random;

/**
 * @author - bageldotjpg
 */
public final class TreeUtil {

	public static void placeLogAt(LevelWriter level, BlockPos pos, Random rand, TreeConfiguration config) {
		setForcedState(level, pos, config.trunkProvider.getState(rand, pos));
	}

	public static void placeDirectionalLogAt(LevelWriter level, BlockPos pos, Direction direction, Random rand, TreeConfiguration config) {
		setForcedState(level, pos, config.trunkProvider.getState(rand, pos).setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
	}

	public static boolean isInTag(LevelSimulatedReader level, BlockPos pos, Named<Block> tag) {
		return level.isStateAtPosition(pos, (block) -> block.is(tag));
	}

	public static void placeLeafAt(LevelSimulatedRW level, BlockPos pos, Random rand, TreeConfiguration config) {
		if (isAirOrLeaves(level, pos)) {
			setForcedState(level, pos, config.foliageProvider.getState(rand, pos).setValue(LeavesBlock.DISTANCE, 1));
		}
	}

	public static void setForcedState(LevelWriter level, BlockPos pos, BlockState state) {
		level.setBlock(pos, state, 18);
	}

	public static boolean isLog(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, (state) -> state.is(BlockTags.LOGS));
	}

	public static boolean isLeaves(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, (state) -> state.is(BlockTags.LEAVES));
	}

	public static boolean isAirOrLeaves(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, (state) -> state.isAir() || state.is(BlockTags.LEAVES));
	}

	public static void setDirtAt(LevelAccessor level, BlockPos pos) {
		Block block = level.getBlockState(pos).getBlock();
		if (block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND) {
			level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 18);
		}
	}

	public static boolean isValidGround(LevelAccessor level, BlockPos pos, SaplingBlock sapling) {
		return level.getBlockState(pos).canSustainPlant(level, pos, Direction.UP, sapling);
	}

}