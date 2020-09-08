package com.teamabnormals.abnormals_core.common.items;

import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InjectedItem extends Item {
	private final Item followItem;

	public InjectedItem(Item followItem, Properties properties) {
		super(properties);
		this.followItem = followItem;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtil.fillAfterItemForGroup(this, this.followItem, group, items);
	}
}