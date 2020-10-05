package com.teamabnormals.abnormals_core.common.items;

import com.teamabnormals.abnormals_core.core.utils.ItemStackUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SignItem;
import net.minecraft.util.NonNullList;

public class AbnormalsSignItem extends SignItem {
	
	public AbnormalsSignItem(Block floorBlockIn, Block wallBlockIn, Item.Properties propertiesIn) {
		super(propertiesIn, floorBlockIn, wallBlockIn);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtils.fillAfterItemForGroup(this.asItem(), Items.WARPED_SIGN, group, items);
	}
}