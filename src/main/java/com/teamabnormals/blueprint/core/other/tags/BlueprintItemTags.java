package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BlueprintItemTags {
	public static final TagKey<Item> CHICKEN_FOOD = itemTag("chicken_food");
	public static final TagKey<Item> PIG_FOOD = itemTag("pig_food");
	public static final TagKey<Item> STRIDER_FOOD = itemTag("strider_food");
	public static final TagKey<Item> STRIDER_TEMPT_ITEMS = itemTag("strider_tempt_items");
	public static final TagKey<Item> OCELOT_FOOD = itemTag("ocelot_food");
	public static final TagKey<Item> CAT_FOOD = itemTag("cat_food");

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

	public static final TagKey<Item> FURNACE_BOATS = TagUtil.itemTag("boatload", "furnace_boats");
	public static final TagKey<Item> LARGE_BOATS = TagUtil.itemTag("boatload", "large_boats");

	private static TagKey<Item> itemTag(String name) {
		return TagUtil.itemTag(Blueprint.MOD_ID, name);
	}
}