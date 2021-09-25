package com.minecraftabnormals.abnormals_core.core.util.item;

import com.minecraftabnormals.abnormals_core.core.mixin.ItemInvokerMixin;
import net.minecraft.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import javax.annotation.Nonnull;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class ItemStackUtil {
	private static final String[] M_NUMERALS = {"", "M", "MM", "MMM"};
	private static final String[] C_NUMERALS = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
	private static final String[] X_NUMERALS = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
	private static final String[] I_NUMERALS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

	/**
	 * Searches for a specific item in a {@link NonNullList} of {@link ItemStack} and returns its index.
	 *
	 * @param item  The item to search for.
	 * @param items The list of {@link ItemStack}s.
	 * @return The index of the specified item in the list, or -1 if it was not in the list.
	 */
	public static int findIndexOfItem(Item item, NonNullList<ItemStack> items) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Used in {@link Item#fillItemCategory(CreativeModeTab, NonNullList)} and {@link Block#fillItemCategory(CreativeModeTab, NonNullList)} to fill an item after a specific item for a group.
	 *
	 * @param item       The item to fill.
	 * @param targetItem The item to fill after.
	 * @param group      The group to fill it in.
	 * @param items      The {@link NonNullList} of item stacks to search for the target item and inject the item in.
	 */
	public static void fillAfterItemForGroup(Item item, Item targetItem, CreativeModeTab group, NonNullList<ItemStack> items) {
		if (isInGroup(item, group)) {
			int targetIndex = findIndexOfItem(targetItem, items);
			if (targetIndex != -1) {
				items.add(targetIndex + 1, new ItemStack(item));
			} else {
				items.add(new ItemStack(item));
			}
		}
	}

	/**
	 * Converts an Integer to a String of Roman Numerals; useful for levels.
	 *
	 * @param number The integer to convert.
	 * @return The integer converted to roman numerals.
	 */
	public static String intToRomanNumerals(int number) {
		String thousands = M_NUMERALS[number / 1000];
		String hundreds = C_NUMERALS[(number % 1000) / 100];
		String tens = X_NUMERALS[(number % 100) / 10];
		String ones = I_NUMERALS[number % 10];
		return thousands + hundreds + tens + ones;
	}

	/**
	 * Checks if an {@link Item} is in an {@link CreativeModeTab}.
	 *
	 * @param item  The {@link Item} to check.
	 * @param group The {@link CreativeModeTab} to check.
	 * @return Whether or not the item is in the {@link CreativeModeTab}.
	 */
	public static boolean isInGroup(Item item, @Nonnull CreativeModeTab group) {
		return ((ItemInvokerMixin) item).callAllowdedIn(group);
	}
}