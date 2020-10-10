package com.teamabnormals.abnormals_core.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
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
	public static final Set<EntityClassification> ALL_CLASSIFICATIONS = Sets.newHashSet(EntityClassification.values());

	private BiomeSpawnsModifier(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
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
	public static BiomeSpawnsModifier createSpawnCost(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Supplier<EntityType<?>> typeSupplier, double spawnCostPerEntity, double maxSpawnCost) {
		return new BiomeSpawnsModifier(shouldModify, context -> context.event.getSpawns().withSpawnCost(typeSupplier.get(), spawnCostPerEntity, maxSpawnCost));
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for adding a {@link MobSpawnInfo.Spawners} for an {@link EntityType}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link EntityClassification} to add the {@link MobSpawnInfo.Spawners} for.
	 * @param typeSupplier   A {@link Supplier} of the {@link EntityType} to add the {@link MobSpawnInfo.Spawners} for.
	 * @param weight         The weight for the {@link MobSpawnInfo.Spawners}.
	 * @param minCount       The minimum spawn count.
	 * @param maxCount       The maximum spawn count.
	 * @return A {@link BiomeSpawnsModifier} for adding a {@link MobSpawnInfo.Spawners} for an {@link EntityType}.
	 */
	public static BiomeSpawnsModifier createSpawnAdder(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, EntityClassification classification, Supplier<EntityType<?>> typeSupplier, int weight, int minCount, int maxCount) {
		return new BiomeSpawnsModifier(shouldModify, context -> context.event.getSpawns().withSpawner(classification, new MobSpawnInfo.Spawners(typeSupplier.get(), weight, minCount, maxCount)));
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnInfo.Spawners} for an {@link EntityClassification}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link EntityClassification} to add the {@link MobSpawnInfo.Spawners} for.
	 * @param spawnInfoSet   A set of {@link SpawnInfo}s to add.
	 * @return A {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnInfo.Spawners} for an {@link EntityClassification}.
	 */
	public static BiomeSpawnsModifier createMultiSpawnAdder(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, EntityClassification classification, Set<SpawnInfo> spawnInfoSet) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			for (SpawnInfo spawnInfo : spawnInfoSet) {
				builder.withSpawner(classification, new MobSpawnInfo.Spawners(spawnInfo.type.get(), spawnInfo.weight, spawnInfo.minCount, spawnInfo.maxCount));
			}
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnInfo.Spawners} for multiple {@link EntityClassification}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param classifications A set of {@link EntityClassification} to add the {@link MobSpawnInfo.Spawners} for.
	 * @param spawnInfoSet    A set of {@link SpawnInfo}s to add.
	 * @return A {@link BiomeSpawnsModifier} for adding multiple {@link MobSpawnInfo.Spawners} for an {@link EntityClassification}.
	 */
	public static BiomeSpawnsModifier createMultiSpawnAdder(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<EntityClassification> classifications, Set<SpawnInfo> spawnInfoSet) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			for (EntityClassification classification : classifications) {
				for (SpawnInfo spawnInfo : spawnInfoSet) {
					builder.withSpawner(classification, new MobSpawnInfo.Spawners(spawnInfo.type.get(), spawnInfo.weight, spawnInfo.minCount, spawnInfo.maxCount));
				}
			}
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for removing {@link MobSpawnInfo.Spawners} entries for {@link EntityType} of a {@link EntityClassification}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link EntityClassification} to remove the {@link MobSpawnInfo.Spawners} for.
	 * @param typeSupplier   A {@link Supplier} of the {@link EntityType} to remove the {@link MobSpawnInfo.Spawners} for.
	 * @return A {@link BiomeSpawnsModifier} for removing {@link MobSpawnInfo.Spawners} entries for {@link EntityType} of a {@link EntityClassification}.
	 */
	public static BiomeSpawnsModifier createSpawnRemover(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, EntityClassification classification, Supplier<EntityType<?>> typeSupplier) {
		return new BiomeSpawnsModifier(shouldModify, context -> context.event.getSpawns().withSpawner(classification, new MobSpawnInfo.Spawners(typeSupplier.get(), 0, 0, 0)));
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for removing {@link MobSpawnInfo.Spawners} entries for multiple {@link EntityType}s of a {@link EntityClassification}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link EntityClassification} to remove the {@link MobSpawnInfo.Spawners} for.
	 * @param typeSuppliers  A set of {@link Supplier}s of the {@link EntityType}s to remove the {@link MobSpawnInfo.Spawners} for.
	 * @return A {@link BiomeSpawnsModifier} for removing {@link MobSpawnInfo.Spawners} entries for multiple {@link EntityType}s of a {@link EntityClassification}.
	 */
	public static BiomeSpawnsModifier createMultiSpawnRemover(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, EntityClassification classification, Set<Supplier<EntityType<?>>> typeSuppliers) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			Set<EntityType<?>> entityTypes = Sets.newHashSet();
			typeSuppliers.forEach(typeSupplier -> entityTypes.add(typeSupplier.get()));
			context.event.getSpawns().spawners.get(classification).removeIf(info -> entityTypes.contains(info.type));
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for removing {@link MobSpawnInfo.Spawners} entries for multiple {@link EntityType}s of multiple {@link EntityClassification}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param classifications A set of {@link EntityClassification}s to remove the {@link MobSpawnInfo.Spawners} for.
	 * @param typeSuppliers   A set of {@link Supplier}s of the {@link EntityType}s to remove the {@link MobSpawnInfo.Spawners} for.
	 * @return A {@link BiomeSpawnsModifier} for removing {@link MobSpawnInfo.Spawners} entries for multiple {@link EntityType}s of multiple {@link EntityClassification}s.
	 */
	public static BiomeSpawnsModifier createMultiSpawnRemover(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<EntityClassification> classifications, Set<Supplier<EntityType<?>>> typeSuppliers) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			Set<EntityType<?>> entityTypes = Sets.newHashSet();
			typeSuppliers.forEach(typeSupplier -> entityTypes.add(typeSupplier.get()));
			for (EntityClassification classification : classifications) {
				context.event.getSpawns().spawners.get(classification).removeIf(info -> entityTypes.contains(info.type));
			}
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for replacing {@link MobSpawnInfo.Spawners} entries for an {@link EntityType} of a {@link EntityClassification}.
	 *
	 * @param shouldModify   A {@link BiPredicate} for what biomes it should modify.
	 * @param classification The {@link EntityClassification} to replace the {@link MobSpawnInfo.Spawners} for.
	 * @param replace        A {@link Supplier} of the {@link EntityType} that will be replaced.
	 * @param replacer       A {@link Supplier} of the {@link EntityType} to replace the {@link MobSpawnInfo.Spawners} for.
	 * @return A {@link BiomeSpawnsModifier} for replacing {@link MobSpawnInfo.Spawners} entries for an {@link EntityType} of a {@link EntityClassification}.
	 */
	public static BiomeSpawnsModifier createSpawnReplacer(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, EntityClassification classification, Supplier<EntityType<?>> replace, Supplier<EntityType<?>> replacer) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			EntityType<?> replaceType = replace.get();
			Set<MobSpawnInfo.Spawners> toRemove = Sets.newHashSet();
			List<MobSpawnInfo.Spawners> spawners = builder.spawners.get(classification);
			for (MobSpawnInfo.Spawners spawner : spawners) {
				if (spawner.type == replaceType) {
					toRemove.add(spawner);
				}
			}
			EntityType<?> replacerType = replacer.get();
			toRemove.forEach(spawner -> {
				spawners.remove(spawner);
				spawners.add(new MobSpawnInfo.Spawners(replacerType, spawner.itemWeight, spawner.minCount, spawner.maxCount));
			});
		});
	}

	/**
	 * Creates a {@link BiomeSpawnsModifier} for replacing {@link MobSpawnInfo.Spawners} entries for an {@link EntityType} for multiple {@link EntityClassification}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param classifications A set of {@link EntityClassification}s to replace the {@link MobSpawnInfo.Spawners} for.
	 * @param replace         A {@link Supplier} of the {@link EntityType} that will be replaced.
	 * @param replacer        A {@link Supplier} of the {@link EntityType} to replace the {@link MobSpawnInfo.Spawners} for.
	 * @return A {@link BiomeSpawnsModifier} for replacing {@link MobSpawnInfo.Spawners} entries for an {@link EntityType} for multiple {@link EntityClassification}s.
	 */
	public static BiomeSpawnsModifier createMultiSpawnReplacer(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<EntityClassification> classifications, Supplier<EntityType<?>> replace, Supplier<EntityType<?>> replacer) {
		return new BiomeSpawnsModifier(shouldModify, context -> {
			MobSpawnInfoBuilder builder = context.event.getSpawns();
			EntityType<?> replaceType = replace.get();
			EntityType<?> replacerType = replacer.get();
			for (EntityClassification classification : classifications) {
				Set<MobSpawnInfo.Spawners> toRemove = Sets.newHashSet();
				List<MobSpawnInfo.Spawners> spawners = builder.spawners.get(classification);
				for (MobSpawnInfo.Spawners spawner : spawners) {
					if (spawner.type == replaceType) {
						toRemove.add(spawner);
					}
				}
				toRemove.forEach(spawner -> {
					spawners.remove(spawner);
					spawners.add(new MobSpawnInfo.Spawners(replacerType, spawner.itemWeight, spawner.minCount, spawner.maxCount));
				});
			}
		});
	}

	public static final class SpawnInfo {
		private final Supplier<EntityType<?>> type;
		private final int weight;
		private final int minCount;
		private final int maxCount;

		public SpawnInfo(Supplier<EntityType<?>> type, int weight, int minCount, int maxCount) {
			this.type = type;
			this.weight = weight;
			this.minCount = minCount;
			this.maxCount = maxCount;
		}
	}
}
