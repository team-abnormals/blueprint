package com.minecraftabnormals.abnormals_core.common.entity;

import com.minecraftabnormals.abnormals_core.core.registry.ACEntities;
import com.minecraftabnormals.abnormals_core.core.registry.BoatRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

/**
 * @author SmellyModder (Luke Tonon)
 */
public class AbnormalsBoatEntity extends Boat {
	private static final EntityDataAccessor<String> BOAT_TYPE = SynchedEntityData.defineId(AbnormalsBoatEntity.class, EntityDataSerializers.STRING);

	public AbnormalsBoatEntity(EntityType<? extends Boat> type, Level world) {
		super(type, world);
		this.blocksBuilding = true;
	}

	public AbnormalsBoatEntity(Level worldIn, double x, double y, double z) {
		this(ACEntities.BOAT.get(), worldIn);
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	public AbnormalsBoatEntity(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
		this(ACEntities.BOAT.get(), world);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(BOAT_TYPE, "minecraft:oak");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putString("Type", BoatRegistry.getNameForData(this.getBoat()));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("Type", Constants.NBT.TAG_STRING)) {
			String type = compound.getString("Type");
			BoatRegistry.BoatData data = BoatRegistry.getDataForBoat(type);
			if (data != null) this.setBoat(BoatRegistry.getNameForData(data));
			else this.setBoat(BoatRegistry.getBaseBoatName());
		} else {
			this.setBoat(BoatRegistry.getBaseBoatName());
		}
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		this.lastYd = this.getDeltaMovement().y;
		if (!this.isPassenger()) {
			if (onGroundIn) {
				if (this.fallDistance > 3.0F) {
					if (this.status != AbnormalsBoatEntity.Status.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}

					this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
					if (!this.level.isClientSide && this.isAlive()) {
						this.kill();
						if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							for (int i = 0; i < 3; ++i) {
								this.spawnAtLocation(this.getBoat().getPlankItem());
							}

							for (int j = 0; j < 2; ++j) {
								this.spawnAtLocation(Items.STICK);
							}
						}
					}
				}

				this.fallDistance = 0.0F;
			} else if (!this.level.getFluidState((new BlockPos(this.position())).below()).is(FluidTags.WATER) && y < 0.0D) {
				this.fallDistance = (float) ((double) this.fallDistance - y);
			}
		}
	}

	@Override
	public Item getDropItem() {
		return this.getBoat().getBoatItem();
	}

	public void setBoat(String boat) {
		this.entityData.set(BOAT_TYPE, boat);
	}

	public BoatRegistry.BoatData getBoat() {
		return BoatRegistry.getDataForBoat(this.entityData.get(BOAT_TYPE));
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}