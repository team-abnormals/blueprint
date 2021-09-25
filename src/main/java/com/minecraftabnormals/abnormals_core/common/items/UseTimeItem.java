package com.minecraftabnormals.abnormals_core.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Item.Properties;

public class UseTimeItem extends Item {
	private final int useTime;

	public UseTimeItem(int useTime, Properties properties) {
		super(properties);
		this.useTime = useTime;
	}

	@Override
	public int getUseDuration(ItemStack itemStack) {
		return this.useTime;
	}
}