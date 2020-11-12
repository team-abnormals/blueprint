package com.teamabnormals.abnormals_core.core.util.item.filling;

import com.teamabnormals.abnormals_core.core.util.item.ItemStackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Implementation class of {@link IItemGroupFiller} for filling {@link Item}s alphabetically.
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
				Optional<ItemStack> optional = items.stream().filter(stack -> this.shouldInclude.test(stack.getItem())).max((stack1, stack2) -> {
					ResourceLocation resourceLocation1 = stack1.getItem().getRegistryName();
					ResourceLocation resourceLocation2 = stack2.getItem().getRegistryName();
					if (resourceLocation1 != null && resourceLocation2 != null) {
						return resourceLocation2.getPath().compareTo(itemName) - itemName.compareTo(resourceLocation1.getPath());
					}
					return 0;
				});
				if (optional.isPresent()) {
					items.add(items.indexOf(optional.get()) + 1, new ItemStack(item));
				} else {
					items.add(new ItemStack(item));
				}
			} else {
				items.add(new ItemStack(item));
			}
		}
	}
}
