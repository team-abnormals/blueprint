package com.minecraftabnormals.abnormals_core.common.entity;

import com.minecraftabnormals.abnormals_core.core.api.IBucketableEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * A {@link WaterAnimal} extension that implements {@link IBucketableEntity}.
 * <p>This is simply a {@link WaterAnimal} that can be bucketed.</p>
 *
 * @see IBucketableEntity
 */
public abstract class BucketableWaterAnimal extends WaterAnimal implements IBucketableEntity {
	private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(BucketableWaterAnimal.class, EntityDataSerializers.BOOLEAN);

	public BucketableWaterAnimal(EntityType<? extends BucketableWaterAnimal> type, Level level) {
		super(type, level);
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

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("FromBucket", this.isFromBucket());
	}

	public void readAdditionalSaveData(CompoundTag compound) {
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
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
			this.playSound(this.getBucketFillSound(), 1.0F, 1.0F);
			itemstack.shrink(1);
			ItemStack itemstack1 = this.getBucket();
			this.setBucketData(itemstack1);
			if (!this.level.isClientSide) {
				CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack1);
			}

			if (itemstack.isEmpty()) {
				player.setItemInHand(hand, itemstack1);
			} else if (!player.getInventory().add(itemstack1)) {
				player.drop(itemstack1, false);
			}

			this.kill();
			return InteractionResult.SUCCESS;
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