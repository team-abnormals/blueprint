package com.teamabnormals.abnormals_core.common.items;

import com.teamabnormals.abnormals_core.core.utils.ItemStackUtils;

import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.NonNullList;

public class AbnormalsBannerPatternItem extends BannerPatternItem {

	public AbnormalsBannerPatternItem(BannerPattern pattern, Properties builder) {
		super(pattern, builder);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtils.fillAfterItemForGroup(this.getItem(), Items.PIGLIN_BANNER_PATTERN, group, items);
	}
}
