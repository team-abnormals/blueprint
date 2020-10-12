package com.teamabnormals.abnormals_core.core.util.item.filling;

import com.google.common.collect.Maps;
import com.teamabnormals.abnormals_core.core.util.item.ItemStackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation class of {@link IItemGroupFiller} for filling {@link Item}s alphabetically.
 *
 * @author SmellyModder (Luke Tonon)
 * @see IItemGroupFiller
 */
public final class AlphabeticalItemGroupFiller implements IItemGroupFiller {
	private static final Pattern NAME_PATTERN = Pattern.compile("(\\w+)[.](\\w+)[.](\\w+)");
	private final Predicate<Item> shouldInclude;

	public AlphabeticalItemGroupFiller(Predicate<Item> shouldInclude) {
		this.shouldInclude = shouldInclude;
	}

	/**
	 * Creates an {@link AlphabeticalItemGroupFiller} that fills items alphabetically for items that are an instance of a class. (e.g. Having a modded spawn egg filled alphabetically into the vanilla's spawn eggs)
	 *
	 * @param clazz The class to test for.
	 * @param <I>   The type of the class, must extend {@link Item}.
	 * @return An {@link AlphabeticalItemGroupFiller} that fills items alphabetically for items that are an instance of a class. (e.g. Having a modded spawn egg filled alphabetically into the vanilla's spawn eggs)
	 */
	public static <I extends Item> AlphabeticalItemGroupFiller forClass(Class<I> clazz) {
		return new AlphabeticalItemGroupFiller(clazz::isInstance);
	}

	@Override
	public void fillItem(Item itemIn, ItemGroup group, NonNullList<ItemStack> items) {
		if (ItemStackUtil.isInGroup(itemIn, group)) {
			Map<String, Integer> nameToIndex = Util.make(Maps.newHashMap(), (map) -> {
				for (int i = 0; i < items.size(); i++) {
					Item item = items.get(i).getItem();
					if (this.shouldInclude.test(item)) {
						Matcher compareMatcher = NAME_PATTERN.matcher(item.getTranslationKey());
						if (compareMatcher.matches()) {
							map.put(compareMatcher.group(3), i);
						}
					}
				}
			});
			if (!nameToIndex.isEmpty()) {
				Matcher matcher = NAME_PATTERN.matcher(itemIn.getTranslationKey());
				if (matcher.matches()) {
					String name = matcher.group(3);
					List<String> list = new ArrayList<>(nameToIndex.keySet());
					list.add(name);
					Collections.sort(list);
					int index = list.indexOf(name) - 1;
					if (index == -1) {
						items.add(nameToIndex.get(list.get(1)), new ItemStack(itemIn));
					} else {
						items.add(nameToIndex.get(list.get(index)) + 1, new ItemStack(itemIn));
					}
				}
			} else {
				items.add(new ItemStack(itemIn));
			}
		}
	}
}
