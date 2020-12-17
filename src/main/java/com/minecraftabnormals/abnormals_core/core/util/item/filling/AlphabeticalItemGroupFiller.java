package com.minecraftabnormals.abnormals_core.core.util.item.filling;

import com.minecraftabnormals.abnormals_core.core.util.item.ItemStackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

/**
 * Implementation class of {@link IItemGroupFiller} for filling {@link Item}s alphabetically.
 * <p>{@link #shouldInclude} is used to test what items should be considered when inserting an item at its alphabetical position.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see IItemGroupFiller
 */
public final class AlphabeticalItemGroupFiller implements IItemGroupFiller {
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
	public void fillItem(Item item, ItemGroup group, NonNullList<ItemStack> items) {
		if (ItemStackUtil.isInGroup(item, group)) {
			ResourceLocation location = item.getRegistryName();
			if (location != null) {
				String itemName = location.getPath();
				int insert = -1;
				for (int i = 0; i < items.size(); i++) {
					Item next = items.get(i).getItem();
					if (this.shouldInclude.test(next)) {
						ResourceLocation nextName = next.getRegistryName();
						if (nextName == null || itemName.compareTo(nextName.getPath()) > 0) {
							insert = i;
						} else {
							break;
						}
					}
				}
				if (insert == -1) {
					items.add(new ItemStack(item));
				} else {
					items.add(insert + 1, new ItemStack(item));
				}
			} else {
				items.add(new ItemStack(item));
			}
		}
	}
}
