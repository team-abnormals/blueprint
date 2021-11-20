package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class BlueprintBlockTags {
	public static final Tag.Named<Block> HEDGES = TagUtil.blockTag("quark", "hedges");
	public static final Tag.Named<Block> LADDERS = TagUtil.blockTag("quark", "ladders");
	public static final Tag.Named<Block> VERTICAL_SLABS = TagUtil.blockTag("quark", "vertical_slabs");
	public static final Tag.Named<Block> WOODEN_VERTICAL_SLABS = TagUtil.blockTag("quark", "wooden_vertical_slabs");
}
