package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlueprintBlockTags {
	public static final TagKey<Block> NOTE_BLOCK_TOP_INSTRUMENTS = TagUtil.blockTag(Blueprint.MOD_ID, "noteblock_top_instruments");
	public static final TagKey<Block> LEAF_PILES = TagUtil.blockTag("woodworks", "leaf_piles");
}
