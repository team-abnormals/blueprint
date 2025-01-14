package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.entity.BlueprintBoat;
import com.teamabnormals.blueprint.common.entity.BlueprintChestBoat;
import com.teamabnormals.blueprint.common.entity.BlueprintFallingBlockEntity;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.EntitySubRegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registry class for the built-in {@link EntityType}s.
 */
@EventBusSubscriber(modid = Blueprint.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class BlueprintEntityTypes {
	private static final EntitySubRegistryHelper HELPER = Blueprint.REGISTRY_HELPER.getEntitySubHelper();

	public static final DeferredHolder<EntityType<?>, EntityType<BlueprintBoat>> BOAT = HELPER.createEntity("boat", BlueprintBoat::new, MobCategory.MISC, builder -> {
		builder.sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10);
	});
	public static final DeferredHolder<EntityType<?>, EntityType<BlueprintChestBoat>> CHEST_BOAT = HELPER.createEntity("chest_boat", BlueprintChestBoat::new, MobCategory.MISC, builder -> {
		builder.sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10);
	});
	public static final DeferredHolder<EntityType<?>, EntityType<BlueprintFallingBlockEntity>> FALLING_BLOCK = HELPER.createEntity("falling_block", BlueprintFallingBlockEntity::new, MobCategory.MISC, builder -> {
		builder.sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20);
	});
}