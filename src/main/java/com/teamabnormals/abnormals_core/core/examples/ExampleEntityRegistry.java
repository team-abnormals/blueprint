package com.teamabnormals.abnormals_core.core.examples;

import com.teamabnormals.abnormals_core.common.entity.AbnormalsBoatEntity;
import com.teamabnormals.abnormals_core.common.entity.ExampleEndimatedEntity;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.util.RegistryHelper;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleEntityRegistry {
	public static final RegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER;
	
	public static final RegistryObject<EntityType<CowEntity>> COW = null; //HELPER.createLivingEntity("example", CowEntity::new, EntityClassification.CREATURE, 1.0F, 1.0F);
	public static final RegistryObject<EntityType<ExampleEndimatedEntity>> EXAMPLE_ANIMATED = null; //HELPER.createLivingEntity("example_animated", ExampleEndimatedEntity::new, EntityClassification.CREATURE, 1.0F, 1.0F);
	public static final RegistryObject<EntityType<AbnormalsBoatEntity>> BOAT = HELPER.createEntity("boat", AbnormalsBoatEntity::new, AbnormalsBoatEntity::new, EntityClassification.MISC, 1.375F, 0.5625F);
}