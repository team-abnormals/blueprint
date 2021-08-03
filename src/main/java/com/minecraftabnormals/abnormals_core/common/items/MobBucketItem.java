package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.common.entity.BucketableWaterMobEntity;
import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MobBucketItem extends BucketItem {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.TROPICAL_FISH_BUCKET);
	private final Supplier<EntityType<? extends BucketableWaterMobEntity>> entityType;

	public MobBucketItem(Supplier<EntityType<? extends BucketableWaterMobEntity>> entityType, Supplier<? extends Fluid> supplier, Item.Properties builder) {
		super(supplier, builder);
		this.entityType = entityType;
	}

	public void checkExtraContent(World world, ItemStack stack, BlockPos pos) {
		if (!world.isClientSide) {
			this.placeEntity((ServerWorld) world, stack, pos);
		}
	}

	protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos) {
		worldIn.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
	}

	protected void placeEntity(ServerWorld world, ItemStack stack, BlockPos pos) {
		Entity entity = this.entityType.get().spawn(world, stack, null, pos, SpawnReason.BUCKET, true, false);
		if (entity != null) {
			((BucketableWaterMobEntity) entity).setFromBucket(true);
		}
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}