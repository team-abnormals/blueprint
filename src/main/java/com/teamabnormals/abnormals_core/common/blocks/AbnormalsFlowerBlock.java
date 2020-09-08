package com.teamabnormals.abnormals_core.common.blocks;

import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

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
		ItemStackUtil.fillAfterItemForGroup(this.asItem(), Items.WITHER_ROSE, group, items);
	}

}
