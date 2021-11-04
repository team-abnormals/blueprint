package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LadderBlock;

/**
 * A {@link LadderBlock} extension that fills its item after the vanilla ladder item.
 */
public class BlueprintLadderBlock extends LadderBlock {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.LADDER);

	public BlueprintLadderBlock(Properties builder) {
		super(builder);
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), tab, list);
	}
}
