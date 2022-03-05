package com.teamabnormals.blueprint.common.item;

import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

/**
 * A {@link RecordItem} extension that fills itself after the latest vanilla music disc item.
 */
public class BlueprintRecordItem extends RecordItem {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.MUSIC_DISC_OTHERSIDE);

	public BlueprintRecordItem(int comparatorValueIn, Supplier<SoundEvent> soundIn, Item.Properties builder) {
		super(comparatorValueIn, soundIn, builder);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}