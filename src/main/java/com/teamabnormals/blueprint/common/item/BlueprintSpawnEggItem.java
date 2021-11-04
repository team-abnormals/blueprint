package com.teamabnormals.blueprint.common.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A {@link SpawnEggItem} extension that supports deferred entity registration.
 *
 * @author SmellyModder(Luke Tonon)
 */
public class BlueprintSpawnEggItem extends SpawnEggItem {
	private static final String ENTITY_TAG = "EntityTag";
	private static final String ENTITY_ID_TAG = "id";
	private final Supplier<EntityType<?>> entityType;

	public BlueprintSpawnEggItem(Supplier<EntityType<?>> entityType, int primaryColor, int secondaryColor, Properties properties) {
		super(null, primaryColor, secondaryColor, properties);
		this.entityType = entityType;
	}

	@Override
	public EntityType<?> getType(@Nullable CompoundTag compound) {
		if (compound != null && compound.contains(ENTITY_TAG, 10)) {
			CompoundTag entityTag = compound.getCompound(ENTITY_TAG);
			if (entityTag.contains(ENTITY_ID_TAG, 8)) {
				return EntityType.byString(entityTag.getString(ENTITY_ID_TAG)).orElse(this.entityType.get());
			}
		}
		return this.entityType.get();
	}
}