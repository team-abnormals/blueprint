package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.entity.BlueprintBoat;
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
}