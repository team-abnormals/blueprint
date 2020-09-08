package com.teamabnormals.abnormals_core.common.blocks;

import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InjectedBlock extends Block {
	private final Item followItem;

	public InjectedBlock(Item followItem, Properties properties) {
		super(properties);
		this.followItem = followItem;
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtil.fillAfterItemForGroup(this.asItem(), this.followItem, group, items);
	}
}