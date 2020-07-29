package com.teamabnormals.abnormals_core.core.library;

import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A class that makes adding automatic entity spawning easier; extend it to use it
 * @author SmellyModder(Luke Tonon)
 */
public abstract class EntitySpawnHandler {
	
	protected static Predicate<Biome> coldOceanCondition() {
		return biome -> biome.getCategory() == Category.OCEAN && BiomeDictionary.hasType(biome, Type.COLD);
	}
	
	protected static Predicate<Biome> notColdOceanCondition() {
		return biome -> biome.getCategory() == Category.OCEAN && !BiomeDictionary.hasType(biome, Type.COLD);
	}
	
	protected static Predicate<Biome> hotOceanCondition() {
		return biome -> biome.getCategory() == Category.OCEAN && BiomeDictionary.hasType(biome, Type.HOT);
	}
	
	protected static Predicate<Biome> warmishOceanCondition() {
		return biome -> biome == Biomes.WARM_OCEAN || biome == Biomes.LUKEWARM_OCEAN;
	}

	protected static class EntitySpawn<T extends MobEntity> {
		public final Supplier<EntityType<T>> entity;
		public final SpawnEntry spawnEntry;
		public final PlacementType placementType;
		public final Heightmap.Type heightmapType;
		public final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate;
		public final Predicate<Biome> biomePredicate;
		
		public EntitySpawn(Supplier<EntityType<T>> entity, SpawnEntry spawnEntry, PlacementType placementType, Heightmap.Type heightmapType, EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate, Predicate<Biome> biomePredicate) {
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
			ForgeRegistries.BIOMES.getEntries().stream().filter(biome -> this.biomePredicate.test(biome.getValue())).forEach((biome) -> {
				biome.getValue().getSpawns(this.spawnEntry.classification).add(new SpawnListEntry(this.entity.get(), this.spawnEntry.weight, this.spawnEntry.minGroup, this.spawnEntry.maxGroup));
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