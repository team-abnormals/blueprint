package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.common.entity.AbnormalsBoat;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.util.registry.EntitySubRegistryHelper;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

/**
 * Registry class for the built-in {@link EntityType}s.
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACEntities {
	private static final EntitySubRegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER.getEntitySubHelper();

	public static final RegistryObject<EntityType<AbnormalsBoat>> BOAT = HELPER.createEntity("boat", AbnormalsBoat::new, AbnormalsBoat::new, MobCategory.MISC, 1.375F, 0.5625F);
}