package com.teamabnormals.abnormals_core.common.items;

import com.google.common.collect.Maps;

import com.teamabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Map;

public class InjectedItem extends Item {
	private static final Map<Item, TargetedItemGroupFiller> FILLER_MAP = Maps.newHashMap();
	private final Item followItem;

	public InjectedItem(Item followItem, Properties properties) {
		super(properties);
		this.followItem = followItem;
		FILLER_MAP.put(followItem, new TargetedItemGroupFiller(() -> followItem));
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER_MAP.get(this.followItem).fillItem(this.asItem(), group, items);
	}
}