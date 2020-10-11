package com.teamabnormals.abnormals_core.common.items;

import java.util.function.Supplier;

import com.teamabnormals.abnormals_core.common.dispenser.SpawnEggDispenseBehavior;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class AbnormalsSpawnEggItem extends SpawnEggItem {
	private Supplier<EntityType<?>> entityType;

	public AbnormalsSpawnEggItem(Supplier<EntityType<?>> entityType, int primaryColor, int secondaryColor, Properties properties) {
		super(null, primaryColor, secondaryColor, properties);
		this.entityType = entityType;
		DispenserBlock.registerDispenseBehavior(this, new SpawnEggDispenseBehavior());
	}

	@Override
	public EntityType<?> getType(CompoundNBT compound) {
		if (compound != null && compound.contains("EntityTag", 10)) {
			CompoundNBT entityTag = compound.getCompound("EntityTag");

			if (entityTag.contains("id", 8)) {
				return EntityType.byKey(entityTag.getString("id")).orElse(this.entityType.get());
			}
		}
		return this.entityType.get();
	}
}