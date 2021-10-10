package com.minecraftabnormals.abnormals_core.core.util.item.filling;

import com.minecraftabnormals.abnormals_core.core.util.item.ItemStackUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

/**
 * Implementation class of {@link IItemCategoryFiller} for filling {@link Item}s alphabetically.
 * <p>{@link #shouldInclude} is used to test what items should be considered when inserting an item at its alphabetical position.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see IItemCategoryFiller
 */
public final class AlphabeticalItemCategoryFiller implements IItemCategoryFiller {
	private final Predicate<Item> shouldInclude;

	public AlphabeticalItemCategoryFiller(Predicate<Item> shouldInclude) {
		this.shouldInclude = shouldInclude;
	}

	/**
	 * Creates an {@link AlphabeticalItemCategoryFiller} that fills items alphabetically for items that are an instance of a class. (e.g. Having a modded spawn egg filled alphabetically into the vanilla's spawn eggs)
	 *
	 * @param clazz The class to test for.
	 * @param <I>   The type of the class, must extend {@link Item}.
	 * @return An {@link AlphabeticalItemCategoryFiller} that fills items alphabetically for items that are an instance of a class. (e.g. Having a modded spawn egg filled alphabetically into the vanilla's spawn eggs)
	 */
	public static <I extends Item> AlphabeticalItemCategoryFiller forClass(Class<I> clazz) {
		return new AlphabeticalItemCategoryFiller(clazz::isInstance);
	}

	@Override
	public void fillItem(Item item, CreativeModeTab group, NonNullList<ItemStack> items) {
		if (ItemStackUtil.isAllowedInTab(item, group)) {
			ResourceLocation location = item.getRegistryName();
			if (location != null) {
				String itemName = location.getPath();
				int insert = -1;
				Predicate<Item> shouldInclude = this.shouldInclude;
				for (int i = 0; i < items.size(); i++) {
					Item next = items.get(i).getItem();
					if (shouldInclude.test(next)) {
						ResourceLocation nextName = next.getRegistryName();
						if (nextName == null || itemName.compareTo(nextName.getPath()) > 0) {
							insert = i + 1;
						} else if (insert == -1) {
							insert += i + 1;
						} else {
							break;
						}
					}
				}
				if (insert == -1) {
					items.add(new ItemStack(item));
				} else {
					items.add(insert, new ItemStack(item));
				}
			} else {
				items.add(new ItemStack(item));
			}
		}
	}
}
