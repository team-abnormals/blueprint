package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.item.*;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;

public class AbnormalsMusicDiscItem extends RecordItem {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.MUSIC_DISC_PIGSTEP);

	public AbnormalsMusicDiscItem(int comparatorValueIn, Supplier<SoundEvent> soundIn, Item.Properties builder) {
		super(comparatorValueIn, soundIn, builder);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}