package com.teamabnormals.abnormals_core.common.blocks;

import com.teamabnormals.abnormals_core.core.utils.ItemStackUtils;

import net.minecraft.block.TallFlowerBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

public class AbnormalsTallFlowerBlock extends TallFlowerBlock {
	
	public AbnormalsTallFlowerBlock(Properties properties) {
        super(properties);
    }
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(ItemStackUtils.isInGroup(this.asItem(), group)) {
			int targetIndex = ItemStackUtils.findIndexOfItem(Items.PEONY, items);
			if(targetIndex != -1) {
				items.add(targetIndex + 1, new ItemStack(this));
			} else {
				super.fillItemGroup(group, items);
			}
		}
	}
}
