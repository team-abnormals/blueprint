package com.teamabnormals.blueprint.common.entity;

import com.teamabnormals.blueprint.client.renderer.HasBlueprintBoatType;
import com.teamabnormals.blueprint.core.registry.BlueprintEntityTypes;
import com.teamabnormals.blueprint.core.registry.BlueprintBoatTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

/**
 * A {@link Boat} extension responsible for Blueprint's boats.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class BlueprintBoat extends Boat implements HasBlueprintBoatType {
	private static final EntityDataAccessor<String> BOAT_TYPE = SynchedEntityData.defineId(BlueprintBoat.class, EntityDataSerializers.STRING);

	public BlueprintBoat(EntityType<? extends Boat> type, Level level) {
		super(type, level);
	}

	public BlueprintBoat(Level level, ResourceLocation type, double x, double y, double z) {
		this(BlueprintEntityTypes.BOAT.get(), level);
		this.setType(type);
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	public BlueprintBoat(PlayMessages.SpawnEntity spawnEntity, Level level) {
		this(BlueprintEntityTypes.BOAT.get(), level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(BOAT_TYPE, BlueprintBoatTypes.UNDEFINED_BOAT_LOCATION.toString());
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putString("Type", this.entityData.get(BOAT_TYPE));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("Type", Tag.TAG_STRING)) {
			String type = compound.getString("Type");
			ResourceLocation name = new ResourceLocation(type);
			if (BlueprintBoatTypes.getType(name) != null) this.setType(name);
			else this.setType(BlueprintBoatTypes.UNDEFINED_BOAT_LOCATION);
		} else {
			this.setType(BlueprintBoatTypes.UNDEFINED_BOAT_LOCATION);
		}
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		this.lastYd = this.getDeltaMovement().y;
		if (!this.isPassenger()) {
			if (onGroundIn) {
				if (this.fallDistance > 3.0F) {
					if (this.status != BlueprintBoat.Status.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}

					this.causeFallDamage(this.fallDistance, 1.0F, this.damageSources().fall());
					if (!this.level().isClientSide && this.isAlive()) {
						this.kill();
						if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							for (int i = 0; i < 3; ++i) {
								this.spawnAtLocation(this.getBoatType().getPlankItem());
							}

							for (int j = 0; j < 2; ++j) {
								this.spawnAtLocation(Items.STICK);
							}
						}
					}
				}

				this.fallDistance = 0.0F;
			} else if (!this.level().getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && y < 0.0D) {
				this.fallDistance = (float) ((double) this.fallDistance - y);
			}
		}
	}

	@Override
	public Item getDropItem() {
		return this.getBoatType().getBoatItem();
	}

	@Override
	public Boat.Type getVariant() {
		return Type.OAK;
	}

	@Override
	public double getPassengersRidingOffset() {
		return this.getBoatType().isRaft() ? 0.25D : -0.1D;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void setType(ResourceLocation type) {
		this.entityData.set(BOAT_TYPE, type.toString());
	}

	@Override
	public BlueprintBoatTypes.BlueprintBoatType getBoatType() {
		return BlueprintBoatTypes.getType(new ResourceLocation(this.entityData.get(BOAT_TYPE)));
	}
}