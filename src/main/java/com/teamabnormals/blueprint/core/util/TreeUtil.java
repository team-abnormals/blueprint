package com.teamabnormals.blueprint.core.util;

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
 * A utility class containing some useful methods for trees.
 *
 * @author bageldotjpg
 */
public final class TreeUtil {
	/**
	 * Places a log at a {@link BlockPos} using a given {@link LevelWriter}, {@link Random}, and {@link TreeConfiguration}.
	 *
	 * @param level  A {@link LevelWriter} to use for placing the log.
	 * @param pos    A {@link BlockPos} for where to place the log.
	 * @param rand   A {@link Random} for randomly selecting the log state.
	 * @param config A {@link TreeConfiguration} to select the log state.
	 */
	public static void placeLogAt(LevelWriter level, BlockPos pos, Random rand, TreeConfiguration config) {
		setForcedState(level, pos, config.trunkProvider.getState(rand, pos));
	}

	/**
	 * Places a directional log at a {@link BlockPos} using a given {@link LevelWriter}, {@link Random}, and {@link TreeConfiguration}.
	 *
	 * @param level     A {@link LevelWriter} to use for placing the log.
	 * @param pos       A {@link BlockPos} for where to place the log.
	 * @param direction The {@link Direction} of the log.
	 * @param rand      A {@link Random} for randomly selecting the log state.
	 * @param config    A {@link TreeConfiguration} to select the log state.
	 */
	public static void placeDirectionalLogAt(LevelWriter level, BlockPos pos, Direction direction, Random rand, TreeConfiguration config) {
		setForcedState(level, pos, config.trunkProvider.getState(rand, pos).setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
	}

	/**
	 * Checks if a {@link BlockPos} has a state at its position in a given tag.
	 *
	 * @param level A {@link LevelSimulatedReader} for getting the state at the given pos.
	 * @param pos   A {@link BlockPos} to look up the block.
	 * @param tag   A tag to check.
	 * @return If a {@link BlockPos} has a state at its position in a given tag.
	 */
	public static boolean isInTag(LevelSimulatedReader level, BlockPos pos, Named<Block> tag) {
		return level.isStateAtPosition(pos, (block) -> block.is(tag));
	}

	/**
	 * Places a foliage block at a {@link BlockPos} using a given {@link LevelSimulatedRW}, {@link Random}, and {@link TreeConfiguration}.
	 *
	 * @param level  A {@link LevelSimulatedRW} for placing the foliage.
	 * @param pos    A {@link BlockPos} for where to place the foliage.
	 * @param rand   A {@link Random} for randomly selecting the foliage state.
	 * @param config A {@link TreeConfiguration} to select the foliage state.
	 */
	public static void placeLeafAt(LevelSimulatedRW level, BlockPos pos, Random rand, TreeConfiguration config) {
		if (isAirOrLeaves(level, pos)) {
			setForcedState(level, pos, config.foliageProvider.getState(rand, pos).setValue(LeavesBlock.DISTANCE, 1));
		}
	}

	/**
	 * Forcefully sets a {@link BlockState} at a given {@link BlockPos}.
	 * <p>Uses flag 18.</p>
	 *
	 * @param level A {@link LevelWriter} to use for placing the {@link BlockState}.
	 * @param pos   A {@link BlockPos} for where to place the {@link BlockState}.
	 * @param state A {@link BlockState} to place.
	 */
	public static void setForcedState(LevelWriter level, BlockPos pos, BlockState state) {
		level.setBlock(pos, state, 18);
	}

	/**
	 * Checks if there is a {@link BlockState} tagged as {@link BlockTags#LOGS} at a given {@link BlockPos}.
	 *
	 * @param level A {@link LevelSimulatedReader} for getting the {@link BlockState}.
	 * @param pos   A {@link BlockPos} for where to check.
	 * @return If there is a {@link BlockState} tagged as {@link BlockTags#LOGS} at a given {@link BlockPos}.
	 */
	public static boolean isLog(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, (state) -> state.is(BlockTags.LOGS));
	}

	/**
	 * Checks if there is a {@link BlockState} tagged as {@link BlockTags#LEAVES} at a given {@link BlockPos}.
	 *
	 * @param level A {@link LevelSimulatedReader} for getting the {@link BlockState}.
	 * @param pos   A {@link BlockPos} for where to check.
	 * @return If there is a {@link BlockState} tagged as {@link BlockTags#LEAVES} at a given {@link BlockPos}.
	 */
	public static boolean isLeaves(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, (state) -> state.is(BlockTags.LEAVES));
	}

	/**
	 * Checks if there is a {@link BlockState} tagged as {@link BlockTags#LEAVES} or is air at a given {@link BlockPos}.
	 *
	 * @param level A {@link LevelSimulatedReader} for getting the {@link BlockState}.
	 * @param pos   A {@link BlockPos} for where to check.
	 * @return If there is a {@link BlockState} tagged as {@link BlockTags#LEAVES} or is air at a given {@link BlockPos}.
	 */
	public static boolean isAirOrLeaves(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, (state) -> state.isAir() || state.is(BlockTags.LEAVES));
	}

	/**
	 * Sets dirt at a given {@link BlockPos} if there is grass or farmland at the given {@link BlockPos}.
	 *
	 * @param level A {@link LevelAccessor} to use.
	 * @param pos   A {@link BlockPos} for where the dirt get placed.
	 */
	public static void setDirtAt(LevelAccessor level, BlockPos pos) {
		Block block = level.getBlockState(pos).getBlock();
		if (block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND) {
			level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 18);
		}
	}

	/**
	 * Checks if the {@link BlockState} at a given {@link BlockPos} can sustain a given {@link SaplingBlock}.
	 *
	 * @param level   A {@link LevelAccessor} to use.
	 * @param pos     A {@link BlockPos} for where to check.
	 * @param sapling A {@link SaplingBlock} to check for.
	 * @return If the {@link BlockState} at a given {@link BlockPos} can sustain a given {@link SaplingBlock}.
	 */
	public static boolean isValidGround(LevelAccessor level, BlockPos pos, SaplingBlock sapling) {
		return level.getBlockState(pos).canSustainPlant(level, pos, Direction.UP, sapling);
	}
}