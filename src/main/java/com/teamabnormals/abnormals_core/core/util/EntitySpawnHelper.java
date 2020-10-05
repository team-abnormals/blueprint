package com.teamabnormals.abnormals_core.core.util;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A class that makes adding automatic entity spawning easier; extend it to use it
 *
 * @author SmellyModder(Luke Tonon)
 */
public abstract class EntitySpawnHelper {

	protected static BiPredicate<RegistryKey<Biome>, Biome> coldOceanCondition() {
		return (key, biome) -> key == Biomes.COLD_OCEAN;
	}

	protected static BiPredicate<RegistryKey<Biome>, Biome> hotOceanCondition() {
		return (key, biome) -> key == Biomes.WARM_OCEAN;
	}

	protected static BiPredicate<RegistryKey<Biome>, Biome> warmishOceanCondition() {
		return (key, biome) -> key == Biomes.WARM_OCEAN || key == Biomes.LUKEWARM_OCEAN;
	}

	protected static class EntitySpawn<T extends MobEntity> {
		public final Supplier<EntityType<T>> entity;
		public final SpawnEntry spawnEntry;
		public final PlacementType placementType;
		public final Heightmap.Type heightmapType;
		public final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate;
		public final BiPredicate<RegistryKey<Biome>, Biome> biomePredicate;

		public EntitySpawn(Supplier<EntityType<T>> entity, SpawnEntry spawnEntry, PlacementType placementType, Heightmap.Type heightmapType, EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate, BiPredicate<RegistryKey<Biome>, Biome> biomePredicate) {
			this.entity = entity;
			this.spawnEntry = spawnEntry;
			this.placementType = placementType;
			this.heightmapType = heightmapType;
			this.placementPredicate = placementPredicate;
			this.biomePredicate = biomePredicate;
		}

		public void registerSpawnPlacement() {
			EntitySpawnPlacementRegistry.register(this.entity.get(), this.placementType, this.heightmapType, this.placementPredicate);
		}

		public void processSpawnAddition() {
			ForgeRegistries.BIOMES.getEntries().stream().filter(biome -> this.biomePredicate.test(biome.getKey(), biome.getValue())).forEach((biome) -> {
				//TODO: Fix immutability.
				//List<Spawners> spawners = biome.getValue().field_242425_l.func_242559_a(this.spawnEntry.classification);
				//spawners.add(new Spawners(this.entity.get(), this.spawnEntry.weight, this.spawnEntry.minGroup, this.spawnEntry.maxGroup));
			});
		}
	}

	protected static class SpawnEntry {
		public final EntityClassification classification;
		public final int weight, minGroup, maxGroup;

		public SpawnEntry(EntityClassification classification, int weight, int minGroup, int maxGroup) {
			this.classification = classification;
			this.weight = weight;
			this.minGroup = minGroup;
			this.maxGroup = maxGroup;
		}
	}

}