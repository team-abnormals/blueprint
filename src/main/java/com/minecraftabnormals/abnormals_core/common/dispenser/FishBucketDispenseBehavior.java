package com.minecraftabnormals.abnormals_core.common.dispenser;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

public final class FishBucketDispenseBehavior extends DefaultDispenseItemBehavior {
	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		BucketItem bucketitem = (BucketItem) stack.getItem();
		BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
		Level world = source.getLevel();
		if (bucketitem.emptyBucket((Player) null, world, blockpos, (BlockHitResult) null)) {
			bucketitem.checkExtraContent(world, stack, blockpos);
			return new ItemStack(Items.BUCKET);
		} else {
			return new DefaultDispenseItemBehavior().dispense(source, stack);
		}
	}
}
