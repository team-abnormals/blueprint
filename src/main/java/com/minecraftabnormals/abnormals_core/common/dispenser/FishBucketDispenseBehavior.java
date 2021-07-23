package com.minecraftabnormals.abnormals_core.common.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public final class FishBucketDispenseBehavior extends DefaultDispenseItemBehavior {
	@Override
	public ItemStack execute(IBlockSource source, ItemStack stack) {
		BucketItem bucketitem = (BucketItem) stack.getItem();
		BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
		World world = source.getLevel();
		if (bucketitem.emptyBucket((PlayerEntity) null, world, blockpos, (BlockRayTraceResult) null)) {
			bucketitem.checkExtraContent(world, stack, blockpos);
			return new ItemStack(Items.BUCKET);
		} else {
			return new DefaultDispenseItemBehavior().dispense(source, stack);
		}
	}
}
