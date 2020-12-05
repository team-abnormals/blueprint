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
	public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		BucketItem bucketitem = (BucketItem) stack.getItem();
		BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
		World world = source.getWorld();
		if (bucketitem.tryPlaceContainedLiquid((PlayerEntity) null, world, blockpos, (BlockRayTraceResult) null)) {
			bucketitem.onLiquidPlaced(world, stack, blockpos);
			return new ItemStack(Items.BUCKET);
		} else {
			return new DefaultDispenseItemBehavior().dispense(source, stack);
		}
	}
}
