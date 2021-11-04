package com.teamabnormals.blueprint.common.item;

import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.core.NonNullList;

/**
 * A {@link BannerPatternItem} extension that fills itself after the latest vanilla banner pattern item.
 */
public class BlueprintBannerPatternItem extends BannerPatternItem {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.PIGLIN_BANNER_PATTERN);

	public BlueprintBannerPatternItem(BannerPattern pattern, Properties builder) {
		super(pattern, builder);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}
