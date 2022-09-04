package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BlueprintItemTags {
	public static final TagKey<Item> EGGS = TagUtil.itemTag("forge", "eggs");
	public static final TagKey<Item> MILK = TagUtil.itemTag("forge", "milk");
	public static final TagKey<Item> PUMPKINS = TagUtil.itemTag("forge", "pumpkins");

	public static final TagKey<Item> TOOLS = TagUtil.itemTag("forge", "tools");
	public static final TagKey<Item> TOOLS_AXES = TagUtil.itemTag("forge", "tools/axes");
	public static final TagKey<Item> TOOLS_HOES = TagUtil.itemTag("forge", "tools/hoes");
	public static final TagKey<Item> TOOLS_PICKAXES = TagUtil.itemTag("forge", "tools/pickaxes");
	public static final TagKey<Item> TOOLS_SHOVELS = TagUtil.itemTag("forge", "tools/shovels");
	public static final TagKey<Item> TOOLS_SWORDS = TagUtil.itemTag("forge", "tools/swords");

	public static final TagKey<Item> BUCKETS = TagUtil.itemTag("forge", "buckets");
	public static final TagKey<Item> BUCKETS_EMPTY = TagUtil.itemTag("forge", "buckets/empty");
	public static final TagKey<Item> BUCKETS_WATER = TagUtil.itemTag("forge", "buckets/water");
	public static final TagKey<Item> BUCKETS_LAVA = TagUtil.itemTag("forge", "buckets/lava");
	public static final TagKey<Item> BUCKETS_MILK = TagUtil.itemTag("forge", "buckets/milk");
	public static final TagKey<Item> BUCKETS_POWDER_SNOW = TagUtil.itemTag("forge", "buckets/powder_snow");

	public static final TagKey<Item> BOATABLE_CHESTS = TagUtil.itemTag("quark", "boatable_chests");
	public static final TagKey<Item> REVERTABLE_CHESTS = TagUtil.itemTag("quark", "revertable_chests");
	public static final TagKey<Item> LADDERS = TagUtil.itemTag("quark", "ladders");
	public static final TagKey<Item> HEDGES = TagUtil.itemTag("quark", "hedges");
	public static final TagKey<Item> VERTICAL_SLABS = TagUtil.itemTag("quark", "vertical_slabs");
	public static final TagKey<Item> WOODEN_VERTICAL_SLABS = TagUtil.itemTag("quark", "wooden_vertical_slabs");
}