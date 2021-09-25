package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.common.entity.BucketableWaterMobEntity;
import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MobBucketItem extends BucketItem {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.TROPICAL_FISH_BUCKET);
	private final Supplier<EntityType<? extends BucketableWaterMobEntity>> entityType;

	public MobBucketItem(Supplier<EntityType<? extends BucketableWaterMobEntity>> entityType, Supplier<? extends Fluid> supplier, Item.Properties builder) {
		super(supplier, builder);
		this.entityType = entityType;
	}

	public void checkExtraContent(Level world, ItemStack stack, BlockPos pos) {
		if (!world.isClientSide) {
			this.placeEntity((ServerLevel) world, stack, pos);
		}
	}

	protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
		worldIn.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
	}

	protected void placeEntity(ServerLevel world, ItemStack stack, BlockPos pos) {
		Entity entity = this.entityType.get().spawn(world, stack, null, pos, MobSpawnType.BUCKET, true, false);
		if (entity != null) {
			((BucketableWaterMobEntity) entity).setFromBucket(true);
		}
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}
}