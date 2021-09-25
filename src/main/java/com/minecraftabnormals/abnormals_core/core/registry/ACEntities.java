package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.common.entity.AbnormalsBoatEntity;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.util.registry.EntitySubRegistryHelper;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACEntities {
	private static final EntitySubRegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER.getEntitySubHelper();

	public static final RegistryObject<EntityType<AbnormalsBoatEntity>> BOAT = HELPER.createEntity("boat", AbnormalsBoatEntity::new, AbnormalsBoatEntity::new, MobCategory.MISC, 1.375F, 0.5625F);
}