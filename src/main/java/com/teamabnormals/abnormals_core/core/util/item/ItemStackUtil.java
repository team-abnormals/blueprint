package com.teamabnormals.abnormals_core.core.util.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class ItemStackUtil {
	/**
	 * Searches for a specific item in a {@link NonNullList} of {@link ItemStack} and returns its index
	 *
	 * @param item  - The item to search for
	 * @param items - The list of ItemStacks
	 * @return - The index of the specified item in the list, if no item was found returns -1
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
	 * Used in {@link Item#fillItemGroup(ItemGroup, NonNullList)} and {@link Block#fillItemGroup(ItemGroup, NonNullList)} to fill an item after a specific item for a group.
	 *
	 * @param item       The item to fill.
	 * @param targetItem The item to fill after.
	 * @param group      The group to fill it in.
	 * @param items      The {@link NonNullList} of item stacks to search for the target item and inject the item in.
	 */
	public static void fillAfterItemForGroup(Item item, Item targetItem, ItemGroup group, NonNullList<ItemStack> items) {
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
	 * Converts an Integer to a String of Roman Numerals; useful for levels
	 *
	 * @param number - The Integer to convert
	 * @return - The String of the Integer converted to Roman Numerals
	 */
	public static String intToRomanNumerals(int number) {
		String m[] = {"", "M", "MM", "MMM"};
		String c[] = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
		String x[] = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
		String i[] = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

		String thousands = m[number / 1000];
		String hundereds = c[(number % 1000) / 100];
		String tens = x[(number % 100) / 10];
		String ones = i[number % 10];

		return thousands + hundereds + tens + ones;
	}

	/**
	 * Searches for if an {@link Item} is present in an {@link ItemGroup} and returns if it is
	 *
	 * @param item  - The item searched
	 * @param group - The group searched
	 * @return - Whether or not the item is present in the group
	 */
	public static boolean isInGroup(Item item, ItemGroup group) {
		if (item.getCreativeTabs().stream().anyMatch(tab -> tab == group)) return true;
		ItemGroup itemgroup = item.getGroup();
		return itemgroup != null && (group == ItemGroup.SEARCH || group == itemgroup);
	}
}