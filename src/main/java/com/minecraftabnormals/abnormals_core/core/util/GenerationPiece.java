package com.minecraftabnormals.abnormals_core.core.util;

import java.util.List;
import java.util.function.BiPredicate;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

/**
 * Class that makes checking conditions for placing generation parts easier
 *
 * @author SmellyModder(Luke Tonon)
 */
public class GenerationPiece {
	private final List<BlockPart> blockPieces = Lists.newArrayList();
	private final BiPredicate<IWorld, BlockPart> blockPlaceCondition;

	public GenerationPiece(BiPredicate<IWorld, BlockPart> blockPlaceCondition) {
		this.blockPlaceCondition = blockPlaceCondition;
	}

	/**
	 * Adds a block to this piece
	 *
	 * @param state - The state of the block
	 * @param pos   - The position of the block
	 */
	public void addBlockPiece(BlockState state, BlockPos pos) {
		this.blockPieces.add(new BlockPart(state, pos));
	}

	/**
	 * Checks if all the blocks loaded in this piece can be placed
	 *
	 * @param world - The world to place the piece in
	 * @return - If all the blocks loaded in this piece can be placed
	 */
	public boolean canPlace(IWorld world) {
		for (BlockPart blocks : this.blockPieces) {
			if (!this.blockPlaceCondition.test(world, blocks)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Places the piece
	 *
	 * @param world - The world to place the piece in
	 */
	public void place(IWorld world) {
		for (BlockPart blocks : this.blockPieces) {
			world.setBlockState(blocks.pos, blocks.state, 2);
		}
	}

	/**
	 * Sees if the piece can be placed and then if it can places it
	 *
	 * @param world - The world to place the piece in
	 */
	public void tryToPlace(IWorld world) {
		if (this.canPlace(world)) {
			this.place(world);
		}
	}

	public class BlockPart {
		public final BlockState state;
		public final BlockPos pos;

		public BlockPart(BlockState state, BlockPos pos) {
			this.state = state;
			this.pos = pos;
		}
	}
}