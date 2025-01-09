package com.teamabnormals.blueprint.common.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * A {@link DefaultDispenseItemBehavior} extension used to dispense the contents of a Fish {@link BucketItem}.
 */
public final class FishBucketDispenseItemBehavior extends DefaultDispenseItemBehavior {
	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) stack.getItem();
		BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
		Level level = source.level();
		DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
		if (dispensibleContainerItem.emptyContents(null, level, blockpos, null, stack)) {
			dispensibleContainerItem.checkExtraContent(null, level, stack, blockpos);
			return this.consumeWithRemainder(source, stack, new ItemStack(Items.BUCKET));
		} else {
			return defaultDispenseItemBehavior.dispense(source, stack);
		}
	}
}
