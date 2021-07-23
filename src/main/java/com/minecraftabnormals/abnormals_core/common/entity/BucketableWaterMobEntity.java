package com.minecraftabnormals.abnormals_core.common.entity;

import com.minecraftabnormals.abnormals_core.core.api.IBucketableEntity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public abstract class BucketableWaterMobEntity extends WaterMobEntity implements IBucketableEntity {
	private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.defineId(BucketableWaterMobEntity.class, DataSerializers.BOOLEAN);

	public BucketableWaterMobEntity(EntityType<? extends BucketableWaterMobEntity> type, World world) {
		super(type, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(FROM_BUCKET, false);
	}

	protected void setBucketData(ItemStack bucket) {
		if (this.hasCustomName()) {
			bucket.setHoverName(this.getCustomName());
		}
	}

	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("FromBucket", this.isFromBucket());
	}

	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		this.setFromBucket(compound.getBoolean("FromBucket"));
	}

	public boolean isFromBucket() {
		return this.entityData.get(FROM_BUCKET);
	}

	public void setFromBucket(boolean value) {
		this.entityData.set(FROM_BUCKET, value);
	}

	protected SoundEvent getBucketFillSound() {
		return SoundEvents.BUCKET_FILL_FISH;
	}

	@Override
	public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
			this.playSound(this.getBucketFillSound(), 1.0F, 1.0F);
			itemstack.shrink(1);
			ItemStack itemstack1 = this.getBucket();
			this.setBucketData(itemstack1);
			if (!this.level.isClientSide) {
				CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) player, itemstack1);
			}

			if (itemstack.isEmpty()) {
				player.setItemInHand(hand, itemstack1);
			} else if (!player.inventory.add(itemstack1)) {
				player.drop(itemstack1, false);
			}

			this.remove();
			return ActionResultType.SUCCESS;
		} else {
			return super.mobInteract(player, hand);
		}
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return !this.isFromBucket() && !this.hasCustomName();
	}

	@Override
	public boolean requiresCustomPersistence() {
		return this.isFromBucket();
	}
}