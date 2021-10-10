package com.minecraftabnormals.abnormals_core.core.util.item.filling;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Interface implemented on classes for special filling of {@link Item}s in {@link CreativeModeTab}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface IItemCategoryFiller {
	/**
	 * Fills an {@link Item} for a {@link CreativeModeTab} given a {@link NonNullList} of the {@link ItemStack}s for that {@link CreativeModeTab}.
	 *
	 * @param item  The {@link Item} to fill.
	 * @param tab   The {@link CreativeModeTab} to fill into.
	 * @param items A {@link NonNullList} of the {@link ItemStack}s for the {@link CreativeModeTab}.
	 */
	void fillItem(Item item, CreativeModeTab tab, NonNullList<ItemStack> items);
}
