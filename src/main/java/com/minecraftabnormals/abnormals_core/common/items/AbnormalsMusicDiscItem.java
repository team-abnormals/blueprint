package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;

import java.util.function.Supplier;

public class AbnormalsMusicDiscItem extends MusicDiscItem {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.MUSIC_DISC_PIGSTEP);

	public AbnormalsMusicDiscItem(int comparatorValueIn, Supplier<SoundEvent> soundIn, Item.Properties builder) {
		super(comparatorValueIn, soundIn, builder);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}