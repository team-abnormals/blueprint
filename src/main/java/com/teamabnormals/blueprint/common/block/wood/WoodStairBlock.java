package com.teamabnormals.blueprint.common.block.wood;

import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link BlueprintStairsBlock} extension that fills its item after the latest vanilla wooden stairs item.
 */
public class WoodStairBlock extends StairBlock {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.WARPED_STAIRS);

	public WoodStairBlock(BlockState state, Properties properties) {
		super(state, properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}
