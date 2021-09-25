package com.minecraftabnormals.abnormals_core.common.items;

import com.google.common.collect.Maps;
import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import java.util.Map;

import net.minecraft.world.item.Item.Properties;

public class InjectedBlockItem extends BlockItem {
	private static final Map<Item, TargetedItemGroupFiller> FILLER_MAP = Maps.newHashMap();
	private final Item followItem;

	public InjectedBlockItem(Item followItem, Block block, Properties builder) {
		super(block, builder);
		this.followItem = followItem;
		FILLER_MAP.put(followItem, new TargetedItemGroupFiller(() -> followItem));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER_MAP.get(this.followItem).fillItem(this.asItem(), group, items);
	}
}
