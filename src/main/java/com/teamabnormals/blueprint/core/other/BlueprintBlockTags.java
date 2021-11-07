package com.teamabnormals.blueprint.core.other;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class BlueprintBlockTags {
	public static final Tag.Named<Block> HEDGES = createQuarkTag("hedges");
	public static final Tag.Named<Block> LADDERS = createQuarkTag("ladders");
	public static final Tag.Named<Block> VERTICAL_SLABS = createQuarkTag("vertical_slabs");
	public static final Tag.Named<Block> WOODEN_VERTICAL_SLABS = createQuarkTag("wooden_vertical_slabs");

	public static Tag.Named<Block> createTag(String modid, String name) {
		return BlockTags.bind(modid + ":" + name);
	}

	public static Tag.Named<Block> createForgeTag(String name) {
		return createTag("forge", name);
	}

	public static Tag.Named<Block> createQuarkTag(String name) {
		return createTag("quark", name);
	}
}
