package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlueprintBlockTags {
	public static final TagKey<Block> LEAF_PILES = TagUtil.blockTag("woodworks", "leaf_piles");

	public static final TagKey<Block> HEDGES = TagUtil.blockTag("quark", "hedges");
	public static final TagKey<Block> LADDERS = TagUtil.blockTag("quark", "ladders");
	public static final TagKey<Block> VERTICAL_SLABS = TagUtil.blockTag("quark", "vertical_slabs");
	public static final TagKey<Block> WOODEN_VERTICAL_SLABS = TagUtil.blockTag("quark", "wooden_vertical_slabs");
}
