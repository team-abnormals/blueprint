package com.teamabnormals.abnormals_core.common.entity;

import com.teamabnormals.abnormals_core.core.library.api.IBucketableEntity;

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
    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.createKey(BucketableWaterMobEntity.class, DataSerializers.BOOLEAN);

    public BucketableWaterMobEntity(EntityType<? extends BucketableWaterMobEntity> type, World world) {
        super(type, world);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(FROM_BUCKET, false);
    }

    protected void setBucketData(ItemStack bucket) {
        if(this.hasCustomName()) {
            bucket.setDisplayName(this.getCustomName());
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("FromBucket", this.isFromBucket());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
    }

    public boolean isFromBucket() {
        return this.dataManager.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean value) {
        this.dataManager.set(FROM_BUCKET, value);
    }
    
    protected SoundEvent getBucketFillSound() {
    	return SoundEvents.ITEM_BUCKET_FILL_FISH;
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(this.getBucketFillSound(), 1.0F, 1.0F);
            itemstack.shrink(1);
            ItemStack itemstack1 = this.getBucket();
            this.setBucketData(itemstack1);
            if (!this.world.isRemote) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) player, itemstack1);
            }

            if (itemstack.isEmpty()) {
                player.setHeldItem(hand, itemstack1);
            } else if (!player.inventory.addItemStackToInventory(itemstack1)) {
                player.dropItem(itemstack1, false);
            }

            this.remove();
            return ActionResultType.SUCCESS;
        } else {
        	return super.func_230254_b_(player, hand);
        }
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    @Override
    public boolean preventDespawn() {
    	return this.isFromBucket();
    }
}