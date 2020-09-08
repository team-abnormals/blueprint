package com.teamabnormals.abnormals_core.common.items;

import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InjectedBlockItem extends BlockItem {
	private final Item followItem;
	
	public InjectedBlockItem(Item followItem, Block block, Properties builder) {
		super(block, builder);
		this.followItem = followItem;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtil.fillAfterItemForGroup(this, this.followItem, group, items);
	}
}
