package com.teamabnormals.abnormals_core.common.items;

import com.google.common.base.Supplier;
import com.teamabnormals.abnormals_core.core.utils.ItemStackUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;

public class AbnormalsMusicDiscItem extends MusicDiscItem { 
	
	public AbnormalsMusicDiscItem(int comparatorValueIn, Supplier<SoundEvent> soundIn, Item.Properties builder) {
		super(comparatorValueIn, soundIn, builder);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(this.isInGroup(group)) {
			int targetIndex = ItemStackUtils.findIndexOfItem(Items.MUSIC_DISC_WAIT, items);
			if(targetIndex != -1) {
				items.add(targetIndex + 1, new ItemStack(this));
			} else {
				super.fillItemGroup(group, items);
			}
		}
	}
	
}