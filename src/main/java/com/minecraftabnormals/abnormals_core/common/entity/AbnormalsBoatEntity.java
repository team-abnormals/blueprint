package com.minecraftabnormals.abnormals_core.common.entity;

import com.minecraftabnormals.abnormals_core.core.registry.ACEntities;
import com.minecraftabnormals.abnormals_core.core.registry.BoatRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * @author SmellyModder (Luke Tonon)
 */
public class AbnormalsBoatEntity extends BoatEntity {
	private static final DataParameter<String> BOAT_TYPE = EntityDataManager.defineId(AbnormalsBoatEntity.class, DataSerializers.STRING);

	public AbnormalsBoatEntity(EntityType<? extends BoatEntity> type, World world) {
		super(type, world);
		this.blocksBuilding = true;
	}

	public AbnormalsBoatEntity(World worldIn, double x, double y, double z) {
		this(ACEntities.BOAT.get(), worldIn);
		this.setPos(x, y, z);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	public AbnormalsBoatEntity(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
		this(ACEntities.BOAT.get(), world);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(BOAT_TYPE, "minecraft:oak");
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		compound.putString("Type", BoatRegistry.getNameForData(this.getBoat()));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
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

					this.causeFallDamage(this.fallDistance, 1.0F);
					if (!this.level.isClientSide && this.isAlive()) {
						this.remove();
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
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}