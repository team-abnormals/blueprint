package com.teamabnormals.abnormals_core.common.entity;

import com.teamabnormals.abnormals_core.core.registry.ACEntities;
import com.teamabnormals.abnormals_core.core.registry.BoatRegistry;
import com.teamabnormals.abnormals_core.core.registry.BoatRegistry.BoatData;

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
	private static final DataParameter<String> BOAT_TYPE = EntityDataManager.createKey(AbnormalsBoatEntity.class, DataSerializers.STRING);
	
	public AbnormalsBoatEntity(EntityType<? extends BoatEntity> type, World world) {
		super(type, world);
		this.preventEntitySpawning = true;
	}
	
	public AbnormalsBoatEntity(World worldIn, double x, double y, double z) {
		this(ACEntities.BOAT.get(), worldIn);
		this.setPosition(x, y, z);
		this.setMotion(Vector3d.ZERO);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	public AbnormalsBoatEntity(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
		this(ACEntities.BOAT.get(), world);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(BOAT_TYPE, "minecraft:oak");
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		compound.putString("Type", BoatRegistry.getNameForData(this.getBoat()));
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
    	if (compound.contains("Type", Constants.NBT.TAG_STRING)) {
    		String type = compound.getString("Type");
    		BoatData data = BoatRegistry.getDataForBoat(type);
    		if (data != null) this.setBoat(BoatRegistry.getNameForData(data));
    		else this.setBoat(BoatRegistry.getBaseBoatName());
    	} else {
    		this.setBoat(BoatRegistry.getBaseBoatName());
    	}
    }

	@Override	
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		this.lastYd = this.getMotion().y;
		if (!this.isPassenger()) {
			if (onGroundIn) {
				if (this.fallDistance > 3.0F) {
					if (this.status != AbnormalsBoatEntity.Status.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}

					this.onLivingFall(this.fallDistance, 1.0F);
					if (!this.world.isRemote && this.isAlive()) {
						this.remove();
						if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
							for (int i = 0; i < 3; ++i) {
								this.entityDropItem(this.getBoat().getPlankItem());
							}

							for (int j = 0; j < 2; ++j) {
								this.entityDropItem(Items.STICK);
							}
						}
					}
				}

				this.fallDistance = 0.0F;
			} else if (!this.world.getFluidState((new BlockPos(this.getPositionVec())).down()).isTagged(FluidTags.WATER) && y < 0.0D) {
				this.fallDistance = (float) ((double) this.fallDistance - y);
			}
		}
	}
	
	@Override
	public Item getItemBoat() {
		return this.getBoat().getBoatItem();
	}
	
	public void setBoat(String boat) {
		this.dataManager.set(BOAT_TYPE, boat);
	}

	public BoatData getBoat() {
		return BoatRegistry.getDataForBoat(this.dataManager.get(BOAT_TYPE));
	}
	
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}