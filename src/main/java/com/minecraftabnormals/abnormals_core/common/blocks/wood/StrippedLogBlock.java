package com.minecraftabnormals.abnormals_core.common.blocks.wood;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class StrippedLogBlock extends RotatedPillarBlock {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.STRIPPED_WARPED_STEM);

	public StrippedLogBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}
