package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.NonNullList;

import net.minecraft.item.Item.Properties;

public class AbnormalsBannerPatternItem extends BannerPatternItem {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.PIGLIN_BANNER_PATTERN);

	public AbnormalsBannerPatternItem(BannerPattern pattern, Properties builder) {
		super(pattern, builder);
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}
