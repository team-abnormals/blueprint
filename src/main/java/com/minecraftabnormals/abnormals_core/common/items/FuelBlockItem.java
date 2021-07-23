package com.minecraftabnormals.abnormals_core.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class FuelBlockItem extends BlockItem {
	private int burnTime;

	public FuelBlockItem(Block block, int burnTimeIn, Properties properties) {
		super(block, properties);
		this.burnTime = burnTimeIn;
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		return burnTime;
	}
}
