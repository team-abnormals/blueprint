package com.teamabnormals.blueprint.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A form of {@link BiomeModifier} for modifying spawns of a biome.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeSpawnsModifier extends BiomeModifier {
	public static final Set<MobCategory> ALL_CLASSIFICATIONS = Sets.newHashSet(MobCategory.values());

	private BiomeSpawnsModifier(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
		super(shouldModify, modifier);
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for putting a spawn cost for a {@link EntityType}.
	 *
	 * @param shouldModify       A {@link BiPredicate} for what biomes it should modify.
	 * @param typeSupplier       A {@link Supplier} of the {@link EntityType} to put spawn costs for.
	 * @param spawnCostPerEntity The spawn cost per entity.
	 * @param maxSpawnCost       The max spawn cost.
	 * @return A {@link BiomeSpawnsModifier} for putting a spawn cost for a {@link EntityType}.
	 */
	public static BiomeSpawnsModifier createSpawnCost(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Supplier<EntityType<?>> typeSupplier, double spawnCostPerEntity, double maxSpawnCost) {
		return new BiomeSpawnsModifier(shouldModify, context -> context.event.getSpawns().addMobCharge(typeSupplier.get(), spawnCostPerEntity, maxSpawnCost));
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for adding a {@link MobSpawnSettings.SpawnerData} for an {@link EntityType}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link MobCategory} to add the {@link MobSpawnSettings.SpawnerData} for.
	 * @param typeSupplier   A {@link Supplier} of the {@link EntityType} to add the {@link MobSpawnSettings.SpawnerData} for.
	 * @param weight         The weight for the {@link MobSpawnSettings.SpawnerData}.
	 * @param minCount       The minimum spawn count.
	 * @param maxCount       The maximum spawn count.
	 * @return A {@link BiomeSpawnsModifier} for adding a {@link MobSpawnSettings.SpawnerData} for an {@link EntityType}.
	 */
	public static BiomeSpawnsModifier createSpawnAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, MobCategory classification, Supplier<EntityType<?>> typeSupplier, int weight, int minCount, int maxCount) {
		return new BiomeSpawnsModifier(shouldModify, context -> context.event.getSpawns().addSpawn(classification, new MobSpawnSettings.SpawnerData(typeSupplier.get(), weight, minCount, maxCount)));
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnSettings.SpawnerData} for an {@link MobCategory}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link MobCategory} to add the {@link MobSpawnSettings.SpawnerData} for.
	 * @param spawnInfoSet   A set of {@link SpawnInfo}s to add.
	 * @return A {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnSettings.SpawnerData} for an {@link MobCategory}.
	 */
	public static BiomeSpawnsModifier createMultiSpawnAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, MobCategory classification, Set<SpawnInfo> spawnInfoSet) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			for (SpawnInfo spawnInfo : spawnInfoSet) {
				builder.addSpawn(classification, new MobSpawnSettings.SpawnerData(spawnInfo.type.get(), spawnInfo.weight, spawnInfo.minCount, spawnInfo.maxCount));
			}
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnSettings.SpawnerData} for multiple {@link MobCategory}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param classifications A set of {@link MobCategory} to add the {@link MobSpawnSettings.SpawnerData} for.
	 * @param spawnInfoSet    A set of {@link SpawnInfo}s to add.
	 * @return A {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnSettings.SpawnerData} for an {@link MobCategory}.
	 */
	public static BiomeSpawnsModifier createMultiSpawnAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<MobCategory> classifications, Set<SpawnInfo> spawnInfoSet) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			for (MobCategory classification : classifications) {
				for (SpawnInfo spawnInfo : spawnInfoSet) {
					builder.addSpawn(classification, new MobSpawnSettings.SpawnerData(spawnInfo.type.get(), spawnInfo.weight, spawnInfo.minCount, spawnInfo.maxCount));
				}
			}
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for removing {@link MobSpawnSettings.SpawnerData} entries for {@link EntityType} of a {@link MobCategory}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link MobCategory} to remove the {@link MobSpawnSettings.SpawnerData} for.
	 * @param typeSupplier   A {@link Supplier} of the {@link EntityType} to remove the {@link MobSpawnSettings.SpawnerData} for.
	 * @return A {@link BiomeSpawnsModifier} for removing {@link MobSpawnSettings.SpawnerData} entries for {@link EntityType} of a {@link MobCategory}.
	 */
	public static BiomeSpawnsModifier createSpawnRemover(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, MobCategory classification, Supplier<EntityType<?>> typeSupplier) {
		return new BiomeSpawnsModifier(shouldModify, context -> context.event.getSpawns().addSpawn(classification, new MobSpawnSettings.SpawnerData(typeSupplier.get(), 0, 0, 0)));
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for removing {@link MobSpawnSettings.SpawnerData} entries for multiple {@link EntityType}s of a {@link MobCategory}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link MobCategory} to remove the {@link MobSpawnSettings.SpawnerData} for.
	 * @param typeSuppliers  A set of {@link Supplier}s of the {@link EntityType}s to remove the {@link MobSpawnSettings.SpawnerData} for.
	 * @return A {@link BiomeSpawnsModifier} for removing {@link MobSpawnSettings.SpawnerData} entries for multiple {@link EntityType}s of a {@link MobCategory}.
	 */
	public static BiomeSpawnsModifier createMultiSpawnRemover(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, MobCategory classification, Set<Supplier<EntityType<?>>> typeSuppliers) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			Set<EntityType<?>> entityTypes = Sets.newHashSet();
			typeSuppliers.forEach(typeSupplier -> entityTypes.add(typeSupplier.get()));
			context.event.getSpawns().spawners.get(classification).removeIf(info -> entityTypes.contains(info.type));
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for removing {@link MobSpawnSettings.SpawnerData} entries for multiple {@link EntityType}s of multiple {@link MobCategory}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param classifications A set of {@link MobCategory}s to remove the {@link MobSpawnSettings.SpawnerData} for.
	 * @param typeSuppliers   A set of {@link Supplier}s of the {@link EntityType}s to remove the {@link MobSpawnSettings.SpawnerData} for.
	 * @return A {@link BiomeSpawnsModifier} for removing {@link MobSpawnSettings.SpawnerData} entries for multiple {@link EntityType}s of multiple {@link MobCategory}s.
	 */
	public static BiomeSpawnsModifier createMultiSpawnRemover(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<MobCategory> classifications, Set<Supplier<EntityType<?>>> typeSuppliers) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			Set<EntityType<?>> entityTypes = Sets.newHashSet();
			typeSuppliers.forEach(typeSupplier -> entityTypes.add(typeSupplier.get()));
			for (MobCategory classification : classifications) {
				context.event.getSpawns().spawners.get(classification).removeIf(info -> entityTypes.contains(info.type));
			}
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for replacing {@link MobSpawnSettings.SpawnerData} entries for an {@link EntityType} of a {@link MobCategory}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link MobCategory} to replace the {@link MobSpawnSettings.SpawnerData} for.
	 * @param replace        A {@link Supplier} of the {@link EntityType} that will be replaced.
	 * @param replacer       A {@link Supplier} of the {@link EntityType} to replace the {@link MobSpawnSettings.SpawnerData} for.
	 * @return A {@link BiomeSpawnsModifier} for replacing {@link MobSpawnSettings.SpawnerData} entries for an {@link EntityType} of a {@link MobCategory}.
	 */
	public static BiomeSpawnsModifier createSpawnReplacer(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, MobCategory classification, Supplier<EntityType<?>> replace, Supplier<EntityType<?>> replacer) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			EntityType<?> replaceType = replace.get();
			Set<MobSpawnSettings.SpawnerData> toRemove = Sets.newHashSet();
			List<MobSpawnSettings.SpawnerData> spawners = builder.spawners.get(classification);
			for (MobSpawnSettings.SpawnerData spawner : spawners) {
				if (spawner.type == replaceType) {
					toRemove.add(spawner);
				}
			}
			EntityType<?> replacerType = replacer.get();
			toRemove.forEach(spawner -> {
				spawners.remove(spawner);
				spawners.add(new MobSpawnSettings.SpawnerData(replacerType, spawner.getWeight(), spawner.minCount, spawner.maxCount));
			});
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for replacing {@link MobSpawnSettings.SpawnerData} entries for an {@link EntityType} for multiple {@link MobCategory}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param classifications A set of {@link MobCategory}s to replace the {@link MobSpawnSettings.SpawnerData} for.
	 * @param replace         A {@link Supplier} of the {@link EntityType} that will be replaced.
	 * @param replacer        A {@link Supplier} of the {@link EntityType} to replace the {@link MobSpawnSettings.SpawnerData} for.
	 * @return A {@link BiomeSpawnsModifier} for replacing {@link MobSpawnSettings.SpawnerData} entries for an {@link EntityType} for multiple {@link MobCategory}s.
	 */
	public static BiomeSpawnsModifier createMultiSpawnReplacer(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<MobCategory> classifications, Supplier<EntityType<?>> replace, Supplier<EntityType<?>> replacer) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			EntityType<?> replaceType = replace.get();
			EntityType<?> replacerType = replacer.get();
			for (MobCategory classification : classifications) {
				Set<MobSpawnSettings.SpawnerData> toRemove = Sets.newHashSet();
				List<MobSpawnSettings.SpawnerData> spawners = builder.spawners.get(classification);
				for (MobSpawnSettings.SpawnerData spawner : spawners) {
					if (spawner.type == replaceType) {
						toRemove.add(spawner);
					}
				}
				toRemove.forEach(spawner -> {
					spawners.remove(spawner);
					spawners.add(new MobSpawnSettings.SpawnerData(replacerType, spawner.getWeight(), spawner.minCount, spawner.maxCount));
				});
			}
		});
	}

	public static final class SpawnInfo {
		private final Supplier<? extends EntityType<?>> type;
		private final int weight;
		private final int minCount;
		private final int maxCount;

		public SpawnInfo(Supplier<? extends EntityType<?>> type, int weight, int minCount, int maxCount) {
			this.type = type;
			this.weight = weight;
			this.minCount = minCount;
			this.maxCount = maxCount;
		}
	}
}
