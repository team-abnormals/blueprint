package com.teamabnormals.blueprint.core.util;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Class that makes checking conditions for placing generation parts easier.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class GenerationPiece {
	private final List<BlockPart> blockPieces = Lists.newArrayList();
	private final BiPredicate<LevelAccessor, BlockPart> blockPlaceCondition;

	public GenerationPiece(BiPredicate<LevelAccessor, BlockPart> blockPlaceCondition) {
		this.blockPlaceCondition = blockPlaceCondition;
	}

	/**
	 * Adds a block to this piece.
	 *
	 * @param state The state of the block.
	 * @param pos   The position of the block.
	 */
	public void addBlockPiece(BlockState state, BlockPos pos) {
		this.blockPieces.add(new BlockPart(state, pos));
	}

	/**
	 * Checks if all the blocks loaded in this piece can be placed.
	 *
	 * @param level The level to place the piece in.
	 * @return If all the blocks loaded in this piece can be placed.
	 */
	public boolean canPlace(LevelAccessor level) {
		for (BlockPart blocks : this.blockPieces) {
			if (!this.blockPlaceCondition.test(level, blocks)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Places the piece.
	 *
	 * @param level The level to place the piece in.
	 */
	public void place(LevelAccessor level) {
		for (BlockPart blocks : this.blockPieces) {
			level.setBlock(blocks.pos, blocks.state, 2);
		}
	}

	/**
	 * Sees if the piece can be placed and then if it can, places it.
	 *
	 * @param level The level to place the piece in.
	 */
	public void tryToPlace(LevelAccessor level) {
		if (this.canPlace(level)) {
			this.place(level);
		}
	}

	public static class BlockPart {
		public final BlockState state;
		public final BlockPos pos;

		public BlockPart(BlockState state, BlockPos pos) {
			this.state = state;
			this.pos = pos;
		}
	}
}