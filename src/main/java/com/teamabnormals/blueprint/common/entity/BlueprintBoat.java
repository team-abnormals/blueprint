package com.teamabnormals.blueprint.common.entity;

import com.teamabnormals.blueprint.client.renderer.HasBlueprintBoatType;
import com.teamabnormals.blueprint.core.registry.BlueprintBoatTypes;
import com.teamabnormals.blueprint.core.registry.BlueprintEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * A {@link Boat} extension responsible for Blueprint's boats.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class BlueprintBoat extends Boat implements HasBlueprintBoatType {
	private static final EntityDataAccessor<String> BOAT_TYPE = SynchedEntityData.defineId(BlueprintBoat.class, EntityDataSerializers.STRING);

	public BlueprintBoat(EntityType<? extends BlueprintBoat> type, Level level) {
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

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(BOAT_TYPE, BlueprintBoatTypes.UNDEFINED_BOAT_LOCATION.toString());
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity p_352110_) {
		return super.getAddEntityPacket(p_352110_);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putString("Type", this.entityData.get(BOAT_TYPE));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("Type", Tag.TAG_STRING)) {
			String type = compound.getString("Type");
			ResourceLocation name = ResourceLocation.parse(type);
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
	protected Vec3 getPassengerAttachmentPoint(Entity p_294665_, EntityDimensions p_295933_, float p_295585_) {
		float f = this.getSinglePassengerXOffset();
		if (this.getPassengers().size() > 1) {
			int i = this.getPassengers().indexOf(p_294665_);
			if (i == 0) {
				f = 0.2F;
			} else {
				f = -0.6F;
			}

			if (p_294665_ instanceof Animal) {
				f += 0.2F;
			}
		}

		return new Vec3(0.0, this.getBoatType().isRaft() ? (double) (p_295933_.height() * 0.8888889F) : (double) (p_295933_.height() / 3.0F), (double) f)
				.yRot(-this.getYRot() * (float) (Math.PI / 180.0));
	}

	public void setType(ResourceLocation type) {
		this.entityData.set(BOAT_TYPE, type.toString());
	}

	@Override
	public BlueprintBoatTypes.BlueprintBoatType getBoatType() {
		return BlueprintBoatTypes.getType(ResourceLocation.parse(this.entityData.get(BOAT_TYPE)));
	}
}