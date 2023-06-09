package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.entity.BlueprintBoat;
import com.teamabnormals.blueprint.common.entity.BlueprintChestBoat;
import com.teamabnormals.blueprint.common.entity.BlueprintFallingBlockEntity;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.EntitySubRegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry class for the built-in {@link EntityType}s.
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BlueprintEntityTypes {
	private static final EntitySubRegistryHelper HELPER = Blueprint.REGISTRY_HELPER.getEntitySubHelper();

	public static final RegistryObject<EntityType<BlueprintBoat>> BOAT = HELPER.createEntity("boat", BlueprintBoat::new, BlueprintBoat::new, MobCategory.MISC, 1.375F, 0.5625F);
	public static final RegistryObject<EntityType<BlueprintChestBoat>> CHEST_BOAT = HELPER.createEntity("chest_boat", BlueprintChestBoat::new, BlueprintChestBoat::new, MobCategory.MISC, 1.375F, 0.5625F);
	public static final RegistryObject<EntityType<BlueprintFallingBlockEntity>> FALLING_BLOCK = HELPER.createEntity("falling_block", BlueprintFallingBlockEntity::new, BlueprintFallingBlockEntity::new, MobCategory.MISC, 0.98F, 0.98F);
}