package com.minecraftabnormals.abnormals_core.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Item.Properties;

public class FuelItem extends Item {
	private final int burnTime;

	public FuelItem(int burnTime, Properties properties) {
		super(properties);
		this.burnTime = burnTime;
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		return this.burnTime;
	}
}