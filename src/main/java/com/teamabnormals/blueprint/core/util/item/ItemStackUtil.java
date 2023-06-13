package com.teamabnormals.blueprint.core.util.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

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
	 * Returns a predicate that checks if an item stack's item is an instance of a class.
	 *
	 * @param clazz The class to check for.
	 * @return A predicate that checks if an item stack's item is an instance of a class.
	 */
	public static Predicate<ItemStack> is(Class<? extends Item> clazz) {
		return stack -> clazz.isInstance(stack.getItem());
	}
}