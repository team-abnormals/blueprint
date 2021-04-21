package com.minecraftabnormals.abnormals_core.common.items;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class AbnormalsSpawnEggItem extends SpawnEggItem {
	private static final String ENTITY_TAG = "EntityTag";
	private static final String ENTITY_ID_TAG = "id";
	private final Supplier<EntityType<?>> entityType;

	public AbnormalsSpawnEggItem(Supplier<EntityType<?>> entityType, int primaryColor, int secondaryColor, Properties properties) {
		super(null, primaryColor, secondaryColor, properties);
		this.entityType = entityType;
	}

	@Override
	public EntityType<?> getType(@Nullable CompoundNBT compound) {
		if (compound != null && compound.contains(ENTITY_TAG, 10)) {
			CompoundNBT entityTag = compound.getCompound(ENTITY_TAG);
			if (entityTag.contains(ENTITY_ID_TAG, 8)) {
				return EntityType.byKey(entityTag.getString(ENTITY_ID_TAG)).orElse(this.entityType.get());
			}
		}
		return this.entityType.get();
	}
}