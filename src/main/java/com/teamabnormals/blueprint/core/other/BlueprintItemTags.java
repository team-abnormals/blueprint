package com.teamabnormals.blueprint.core.other;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class BlueprintItemTags {
	public static final Tag.Named<Item> BOATABLE_CHESTS = createQuarkTag("boatable_chests");
	public static final Tag.Named<Item> LADDERS = createQuarkTag("ladders");
	public static final Tag.Named<Item> VERTICAL_SLABS = createQuarkTag("vertical_slabs");
	public static final Tag.Named<Item> WOODEN_VERTICAL_SLABS = createQuarkTag("wooden_vertical_slabs");

	public static Tag.Named<Item> createTag(String modid, String name) {
		return ItemTags.bind(modid + ":" + name);
	}

	public static Tag.Named<Item> createForgeTag(String name) {
		return createTag("forge", name);
	}

	public static Tag.Named<Item> createQuarkTag(String name) {
		return createTag("quark", name);
	}
}
