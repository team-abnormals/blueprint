package com.teamabnormals.abnormals_core.common.blocks;

import com.teamabnormals.abnormals_core.core.utils.ItemStackUtils;

import net.minecraft.block.FlowerBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.util.NonNullList;

public class AbnormalsFlowerBlock extends FlowerBlock {
	
	public AbnormalsFlowerBlock(Effect stewEffect, int stewEffectDuration, Properties properties) {
        super(stewEffect, stewEffectDuration, properties);
    }
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (ItemStackUtils.isInGroup(this.asItem(), group)) {
			int targetIndex = ItemStackUtils.findIndexOfItem(Items.WITHER_ROSE, items);
			if (targetIndex != -1) {
				items.add(targetIndex + 1, new ItemStack(this));
			} else {
				super.fillItemGroup(group, items);
			}
		}
	}
}
