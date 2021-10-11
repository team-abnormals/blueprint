package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.core.NonNullList;

/**
 * A {@link BannerPatternItem} extension that fills itself after the latest vanilla banner pattern item.
 */
public class AbnormalsBannerPatternItem extends BannerPatternItem {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.PIGLIN_BANNER_PATTERN);

	public AbnormalsBannerPatternItem(BannerPattern pattern, Properties builder) {
		super(pattern, builder);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}
