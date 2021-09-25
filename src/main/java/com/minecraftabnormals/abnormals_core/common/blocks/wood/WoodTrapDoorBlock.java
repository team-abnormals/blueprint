package com.minecraftabnormals.abnormals_core.common.blocks.wood;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WoodTrapDoorBlock extends TrapDoorBlock {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.WARPED_TRAPDOOR);

	public WoodTrapDoorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}
