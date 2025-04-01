package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlueprintBlockTags {
	public static final TagKey<Block> LADDERS = TagUtil.blockTag("c", "ladders");

	public static final TagKey<Block> ATTACHES_BLOCKS_TO_PISTON = blockTag("attaches_blocks_to_piston");
	public static final TagKey<Block> NOTE_BLOCK_TOP_INSTRUMENTS = blockTag("noteblock_top_instruments");

	public static final TagKey<Block> WOODEN_CHESTS = blockTag("wooden_chests");
	public static final TagKey<Block> WOODEN_TRAPPED_CHESTS = blockTag("wooden_trapped_chests");
	public static final TagKey<Block> WOODEN_LADDERS = blockTag("wooden_ladders");
	public static final TagKey<Block> WOODEN_BEEHIVES = blockTag("wooden_beehives");
	public static final TagKey<Block> WOODEN_BOOKSHELVES = blockTag("wooden_bookshelves");
	public static final TagKey<Block> WOODEN_CHISELED_BOOKSHELVES = blockTag("wooden_chiseled_bookshelves");

	public static final TagKey<Block> WOODEN_BOARDS = TagUtil.blockTag("woodworks", "wooden_boards");
	public static final TagKey<Block> LEAF_PILES = TagUtil.blockTag("woodworks", "leaf_piles");

	private static TagKey<Block> blockTag(String name) {
		return TagUtil.blockTag(Blueprint.MOD_ID, name);
	}
}
