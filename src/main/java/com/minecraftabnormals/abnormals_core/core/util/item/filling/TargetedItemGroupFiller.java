package com.minecraftabnormals.abnormals_core.core.util.item.filling;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.core.util.item.ItemStackUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Implementation class of {@link IItemGroupFiller} for filling {@link Item}s after a target {@link Item}.
 *
 * @author SmellyModder (Luke Tonon)
 * @see IItemGroupFiller
 */
public final class TargetedItemGroupFiller implements IItemGroupFiller {
	private final Supplier<Item> targetItem;
	private final Map<CreativeModeTab, OffsetValue> offsetMap = Maps.newHashMap();

	public TargetedItemGroupFiller(Supplier<Item> targetItem) {
		this.targetItem = targetItem;
	}

	@Override
	public void fillItem(Item item, CreativeModeTab group, NonNullList<ItemStack> items) {
		if (ItemStackUtil.isInGroup(item, group)) {
			OffsetValue offset = this.offsetMap.computeIfAbsent(group, (key) -> new OffsetValue());
			if (offset.itemsProcessed.contains(item)) {
				offset.reset();
			}
			int index = ItemStackUtil.findIndexOfItem(this.targetItem.get(), items);
			if (index != -1) {
				items.add(index + offset.offset, new ItemStack(item));
				offset.itemsProcessed.add(item);
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
		 * Vanilla doesn't cache its item group items so we must make sure the offsets are reset when the process is ran again.
		 */
		private void reset() {
			this.offset = 1;
			this.itemsProcessed.clear();
		}
	}
}
