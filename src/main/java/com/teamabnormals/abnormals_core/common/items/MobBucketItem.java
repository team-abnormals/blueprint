package com.teamabnormals.abnormals_core.common.items;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.teamabnormals.abnormals_core.common.entity.BucketableWaterMobEntity;
import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MobBucketItem extends BucketItem {
	private final Supplier<EntityType<? extends BucketableWaterMobEntity>> entityType;

	public MobBucketItem(Supplier<EntityType<? extends BucketableWaterMobEntity>> entityType, Supplier<? extends Fluid> supplier, Item.Properties builder) {
		super(supplier, builder);
		this.entityType = entityType;
	}

	public void onLiquidPlaced(World world, ItemStack stack, BlockPos pos) {
		if (!world.isRemote) {
			this.placeEntity((ServerWorld) world, stack, pos);
		}
	}
	
	protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos) {
		worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
	}

	protected void placeEntity(ServerWorld world, ItemStack stack, BlockPos pos) {
		Entity entity = this.entityType.get().spawn(world, stack, null, pos, SpawnReason.BUCKET, true, false);
		if (entity != null) {
			((BucketableWaterMobEntity)entity).setFromBucket(true);
		}
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			int targetIndex = ItemStackUtil.findIndexOfItem(Items.TROPICAL_FISH_BUCKET, items);
			if (targetIndex != -1) {
				items.add(targetIndex + 1, new ItemStack(this));
			} else {
				super.fillItemGroup(group, items);
			}
		}
	}
}