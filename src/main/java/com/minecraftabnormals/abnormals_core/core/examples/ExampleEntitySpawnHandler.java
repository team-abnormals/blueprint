package com.minecraftabnormals.abnormals_core.core.examples;

import java.util.List;

import com.google.common.collect.Lists;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.library.EntitySpawnHandler;
import com.minecraftabnormals.abnormals_core.core.library.Test;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author SmellyModder(Luke Tonon)
 */
@Test
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleEntitySpawnHandler extends EntitySpawnHandler {
	private static final List<EntitySpawn<? extends MobEntity>> SPAWNS = Util.make(Lists.newArrayList(), spawns -> {
		spawns.add(new EntitySpawn<CowEntity>(() -> ExampleEntityRegistry.COW.get(), new SpawnEntry(EntityClassification.CREATURE, 8, 1, 3), PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, CowEntity::canAnimalSpawn, (biome) -> biome == Biomes.PLAINS));
	});
	
	private static void registerSpawnPlacements() {
		SPAWNS.forEach(entitySpawns -> {
			entitySpawns.registerSpawnPlacement();
		});
	}
	
	public static void processSpawnAdditions() {
		SPAWNS.forEach(entitySpawns -> {
			entitySpawns.processSpawnAddition();
		});
	}
	
	//@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerEntitySpawns(RegistryEvent.Register<EntityType<?>> event) {
		registerSpawnPlacements();
	}
}