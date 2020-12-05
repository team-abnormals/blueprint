package com.minecraftabnormals.abnormals_core.core.util.item.filling;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Interface implemented on classes for special filling of {@link Item}s in {@link ItemGroup}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface IItemGroupFiller {
	/**
	 * Fills an {@link Item} for an {@link ItemGroup} given a {@link NonNullList} of the {@link ItemStack}s for that {@link ItemGroup}.
	 *
	 * @param item  The {@link Item} to fill.
	 * @param group The {@link ItemGroup} to fill into.
	 * @param items A {@link NonNullList} of the {@link ItemStack}s for the {@link ItemGroup}.
	 */
	void fillItem(Item item, ItemGroup group, NonNullList<ItemStack> items);
}
