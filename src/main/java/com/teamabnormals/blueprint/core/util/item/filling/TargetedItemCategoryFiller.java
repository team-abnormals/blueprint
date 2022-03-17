package com.teamabnormals.blueprint.core.util.item.filling;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.teamabnormals.blueprint.core.util.item.ItemStackUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Implementation class of {@link IItemCategoryFiller} for filling {@link Item}s after a target {@link Item}.
 *
 * @author SmellyModder (Luke Tonon)
 * @see IItemCategoryFiller
 */
public final class TargetedItemCategoryFiller implements IItemCategoryFiller {
	private final Supplier<Item> targetItem;
	private final Map<CreativeModeTab, OffsetValue> offsetMap = Maps.newHashMap();

	public TargetedItemCategoryFiller(Supplier<Item> targetItem) {
		this.targetItem = targetItem;
	}

	@Override
	public void fillItem(Item item, CreativeModeTab tab, NonNullList<ItemStack> items) {
		if (ItemStackUtil.isAllowedInTab(item, tab)) {
			OffsetValue offset = this.offsetMap.computeIfAbsent(tab, (key) -> new OffsetValue());
			Set<Item> itemsProcessed = offset.itemsProcessed;
			if (itemsProcessed.contains(item)) {
				offset.reset();
			}
			int index = ItemStackUtil.findIndexOfItem(this.targetItem.get(), items);
			if (index != -1) {
				items.add(index + offset.offset, new ItemStack(item));
				itemsProcessed.add(item);
				offset.offset++;
			} else {
				items.add(new ItemStack(item));
			}
		}
	}

	static class OffsetValue {
		private final Set<Item> itemsProcessed = Sets.newHashSet();
		private int offset = 1;

		/**
		 * Vanilla doesn't cache its item group items, so we must make sure the offsets are reset when the process is run again.
		 */
		private void reset() {
			this.offset = 1;
			this.itemsProcessed.clear();
		}
	}
}
