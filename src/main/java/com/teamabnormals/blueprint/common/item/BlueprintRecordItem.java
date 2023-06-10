package com.teamabnormals.blueprint.common.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

/**
 * A {@link RecordItem} extension that fills itself after the latest vanilla music disc item.
 */
public class BlueprintRecordItem extends RecordItem {

	public BlueprintRecordItem(int comparatorValueIn, Supplier<SoundEvent> soundIn, Item.Properties builder, int lengthInSeconds) {
		super(comparatorValueIn, soundIn, builder, lengthInSeconds * 20);
	}

}