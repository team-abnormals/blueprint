package com.teamabnormals.blueprint.common.block.wood;

import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.TrapDoorBlock;

/**
 * A {@link TrapDoorBlock} extension that fills its item after the latest vanilla wooden trapdoor item.
 */
public class WoodTrapDoorBlock extends TrapDoorBlock {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.WARPED_TRAPDOOR);

	public WoodTrapDoorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}
