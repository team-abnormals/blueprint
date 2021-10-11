package com.minecraftabnormals.abnormals_core.common.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
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
		BucketItem bucketitem = (BucketItem) stack.getItem();
		BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
		Level level = source.getLevel();
		if (bucketitem.emptyContents(null, level, blockpos, null)) {
			bucketitem.checkExtraContent(null, level, stack, blockpos);
			return new ItemStack(Items.BUCKET);
		} else {
			return new DefaultDispenseItemBehavior().dispense(source, stack);
		}
	}

}
