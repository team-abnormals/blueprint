package com.teamabnormals.abnormals_core.common.blocks.wood;

import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.block.WoodButtonBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

public class AbnormalsWoodButtonBlock extends WoodButtonBlock {

	public AbnormalsWoodButtonBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtil.fillAfterItemForGroup(this.asItem(), Items.field_234748_fa_, group, items);
	}
}
