package com.teamabnormals.abnormals_core.common.items;

import com.teamabnormals.abnormals_core.common.dispenser.SpawnEggDispenseBehavior;
import com.teamabnormals.abnormals_core.core.util.item.filling.AlphabeticalItemGroupFiller;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class AbnormalsSpawnEggItem extends SpawnEggItem {
	private static final AlphabeticalItemGroupFiller FILLER = AlphabeticalItemGroupFiller.forClass(SpawnEggItem.class);
	private static final String ENTITY_TAG = "EntityTag";
	private static final String ENTITY_ID_TAG = "id";
	private final Supplier<EntityType<?>> entityType;

	public AbnormalsSpawnEggItem(Supplier<EntityType<?>> entityType, int primaryColor, int secondaryColor, Properties properties) {
		super(null, primaryColor, secondaryColor, properties);
		this.entityType = entityType;
		DispenserBlock.registerDispenseBehavior(this, new SpawnEggDispenseBehavior());
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
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