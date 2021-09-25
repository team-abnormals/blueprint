package com.minecraftabnormals.abnormals_core.core.util.item.filling;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

/**
 * Interface implemented on classes for special filling of {@link Item}s in {@link CreativeModeTab}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface IItemGroupFiller {
	/**
	 * Fills an {@link Item} for an {@link CreativeModeTab} given a {@link NonNullList} of the {@link ItemStack}s for that {@link CreativeModeTab}.
	 *
	 * @param item  The {@link Item} to fill.
	 * @param group The {@link CreativeModeTab} to fill into.
	 * @param items A {@link NonNullList} of the {@link ItemStack}s for the {@link CreativeModeTab}.
	 */
	void fillItem(Item item, CreativeModeTab group, NonNullList<ItemStack> items);
}
