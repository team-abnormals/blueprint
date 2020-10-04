package core.registry;

import com.google.common.collect.Sets;
import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.util.EntitySpawnHelper;
import core.ACTest;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
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

import java.util.Set;

/**
 * @author SmellyModder(Luke Tonon)
 */
@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestEntitySpawnHelper extends EntitySpawnHelper {
	private static final Set<EntitySpawn<? extends MobEntity>> SPAWNS = Util.make(Sets.newHashSet(), spawns -> {
		spawns.add(new EntitySpawn<>(TestEntities.COW, new SpawnEntry(EntityClassification.CREATURE, 8, 1, 3), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, CowEntity::canAnimalSpawn, (key, biome) -> key == Biomes.PLAINS));
	});

	private static void registerSpawnPlacements() {
		SPAWNS.forEach(EntitySpawn::registerSpawnPlacement);
	}

	public static void processSpawnAdditions() {
		SPAWNS.forEach(EntitySpawn::processSpawnAddition);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerEntitySpawns(RegistryEvent.Register<EntityType<?>> event) {
		registerSpawnPlacements();
	}
}
