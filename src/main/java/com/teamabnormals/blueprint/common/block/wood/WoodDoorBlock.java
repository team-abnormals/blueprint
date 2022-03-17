package com.teamabnormals.blueprint.common.block.wood;

import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DoorBlock;

/**
 * A {@link DoorBlock} extension that fills its item after the latest vanilla wooden door item.
 */
public class WoodDoorBlock extends DoorBlock {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.WARPED_DOOR);

	public WoodDoorBlock(Properties builder) {
		super(builder);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}
