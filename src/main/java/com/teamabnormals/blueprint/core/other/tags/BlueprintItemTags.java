package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class BlueprintItemTags {
	public static final Tag.Named<Item> BUCKETS_EMPTY = TagUtil.forgeItemTag("buckets/empty");
	public static final Tag.Named<Item> BUCKETS_WATER = TagUtil.forgeItemTag("buckets/water");
	public static final Tag.Named<Item> BUCKETS_LAVA = TagUtil.forgeItemTag("buckets/lava");
	public static final Tag.Named<Item> BUCKETS_MILK = TagUtil.forgeItemTag("buckets/milk");
	public static final Tag.Named<Item> BUCKETS_POWDER_SNOW = TagUtil.forgeItemTag("buckets/powder_snow");

	public static final Tag.Named<Item> BOATABLE_CHESTS = TagUtil.itemTag("quark", "boatable_chests");
	public static final Tag.Named<Item> LADDERS = TagUtil.itemTag("quark", "ladders");
	public static final Tag.Named<Item> VERTICAL_SLABS = TagUtil.itemTag("quark", "vertical_slabs");
	public static final Tag.Named<Item> WOODEN_VERTICAL_SLABS = TagUtil.itemTag("quark", "wooden_vertical_slabs");
}
