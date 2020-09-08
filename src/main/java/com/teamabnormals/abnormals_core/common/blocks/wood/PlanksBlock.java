package com.teamabnormals.abnormals_core.common.blocks.wood;

import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

public class PlanksBlock extends Block {
	public PlanksBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtil.fillAfterItemForGroup(this.asItem(), Items.WARPED_PLANKS, group, items);
	}
}