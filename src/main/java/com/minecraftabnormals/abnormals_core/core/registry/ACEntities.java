package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.common.entity.AbnormalsBoatEntity;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.util.registry.EntitySubRegistryHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACEntities {
	private static final EntitySubRegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER.getEntitySubHelper();

	public static final RegistryObject<EntityType<AbnormalsBoatEntity>> BOAT = HELPER.createEntity("boat", AbnormalsBoatEntity::new, AbnormalsBoatEntity::new, EntityClassification.MISC, 1.375F, 0.5625F);
}