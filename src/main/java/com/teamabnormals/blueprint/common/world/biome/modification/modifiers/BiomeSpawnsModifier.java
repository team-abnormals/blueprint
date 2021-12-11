package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.ErrorableOptionalFieldCodec;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;

/**
 * An {@link IBiomeModifier} implementation that modifies the spawns of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeSpawnsModifier implements IBiomeModifier<Pair<Optional<BiomeSpawnsModifier.Spawners>, Optional<BiomeSpawnsModifier.SpawnCosts>>> {
	private static final Codec<Pair<Optional<BiomeSpawnsModifier.Spawners>, Optional<BiomeSpawnsModifier.SpawnCosts>>> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Spawners.CODEC.optionalFieldOf("spawners").forGetter(Pair::getFirst),
				SpawnCosts.CODEC.optionalFieldOf("spawn_costs").forGetter(Pair::getSecond)
		).apply(instance, Pair::of);
	});
	private static final Field MOB_SPAWN_COSTS = ObfuscationReflectionHelper.findField(MobSpawnSettings.Builder.class, "f_48363_");

	private static StringRepresentable resourceLocationRepresentable(ResourceLocation location) {
		return location::toString;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modify(BiomeLoadingEvent event, Pair<Optional<Spawners>, Optional<SpawnCosts>> config) {
		MobSpawnSettingsBuilder spawnSettingsBuilder = event.getSpawns();
		config.getFirst().ifPresent(spawners -> {
			spawners.remove.ifPresent(map -> {
				var mobCategoryListMap = spawnSettingsBuilder.spawners;
				map.forEach((mobCategory, set) -> {
					mobCategoryListMap.get(mobCategory).removeIf(data -> set.contains(data.type));
				});
			});
			spawners.replace.ifPresent(map -> {
				map.forEach((mobCategory, typeMap) -> {
					List<MobSpawnSettings.SpawnerData> toReplace = new ArrayList<>();
					var categorizedSpawners = spawnSettingsBuilder.spawners.get(mobCategory);
					Iterator<MobSpawnSettings.SpawnerData> iterator = categorizedSpawners.iterator();
					while (iterator.hasNext()) {
						MobSpawnSettings.SpawnerData replacer = typeMap.get(iterator.next().type);
						if (replacer != null) {
							iterator.remove();
							toReplace.add(replacer);
						}
					}
					categorizedSpawners.addAll(toReplace);
				});
			});
			spawners.add.ifPresent(map -> {
				map.forEach((mobCategory, list) -> {
					list.forEach(spawnerData -> spawnSettingsBuilder.addSpawn(mobCategory, spawnerData));
				});
			});
		});
		config.getSecond().ifPresent(spawnCosts -> {
			try {
				Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> spawnCostMap = (Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>) MOB_SPAWN_COSTS.get(spawnSettingsBuilder);
				spawnCosts.remove.ifPresent(list -> list.forEach(spawnCostMap::remove));
				spawnCosts.replace.ifPresent(spawnCostMap::putAll);
				spawnCosts.add.ifPresent(map -> {
					map.forEach((type, mobSpawnCost) -> {
						if (!spawnCostMap.containsKey(type)) {
							spawnCostMap.put(type, mobSpawnCost);
						}
					});
				});
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public JsonElement serialize(Pair<Optional<Spawners>, Optional<SpawnCosts>> config, RegistryWriteOps<JsonElement> additional) throws JsonParseException {
		var result = CODEC.encodeStart(additional, config);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return result.result().get();
	}

	@Override
	public Pair<Optional<Spawners>, Optional<SpawnCosts>> deserialize(JsonElement element, RegistryReadOps<JsonElement> additional) throws JsonParseException {
		var result = CODEC.decode(additional, element);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return result.result().get().getFirst();
	}

	public static record Spawners(Optional<Map<MobCategory, HashSet<EntityType<?>>>> remove, Optional<Map<MobCategory, Map<EntityType<?>, MobSpawnSettings.SpawnerData>>> replace, Optional<Map<MobCategory, List<MobSpawnSettings.SpawnerData>>> add) {
		@SuppressWarnings("deprecation")
		private static final Codec<Spawners> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					new ErrorableOptionalFieldCodec<>("remove", Codec.simpleMap(MobCategory.CODEC, Registry.ENTITY_TYPE.byNameCodec().listOf().xmap(HashSet::new, ArrayList::new), StringRepresentable.keys(MobCategory.values())).codec()).forGetter(config -> config.remove),
					new ErrorableOptionalFieldCodec<>("replace", Codec.simpleMap(MobCategory.CODEC, Codec.simpleMap(Registry.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.SpawnerData.CODEC, StringRepresentable.keys(Registry.ENTITY_TYPE.keySet().stream().map(BiomeSpawnsModifier::resourceLocationRepresentable).toArray(StringRepresentable[]::new))).codec(), StringRepresentable.keys(MobCategory.values())).codec()).forGetter(config -> config.replace),
					new ErrorableOptionalFieldCodec<>("add", Codec.simpleMap(MobCategory.CODEC, MobSpawnSettings.SpawnerData.CODEC.listOf().promotePartial(Util.prefix("Spawn data: ", Blueprint.LOGGER::error)), StringRepresentable.keys(MobCategory.values())).codec()).forGetter(config -> config.add)
			).apply(instance, Spawners::new);
		});
	}

	public static record SpawnCosts(Optional<List<EntityType<?>>> remove, Optional<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>> replace, Optional<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>> add) {
		@SuppressWarnings("deprecation")
		private static final Codec<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>> SPAWN_COST_MAP_CODEC = Codec.simpleMap(Registry.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, StringRepresentable.keys(Registry.ENTITY_TYPE.keySet().stream().map(BiomeSpawnsModifier::resourceLocationRepresentable).toArray(StringRepresentable[]::new))).codec();
		@SuppressWarnings("deprecation")
		private static final Codec<SpawnCosts> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					Registry.ENTITY_TYPE.byNameCodec().listOf().optionalFieldOf("remove").forGetter(config -> config.remove),
					SPAWN_COST_MAP_CODEC.optionalFieldOf("replace").forGetter(config -> config.replace),
					SPAWN_COST_MAP_CODEC.optionalFieldOf("add").forGetter(config -> config.add)
			).apply(instance, SpawnCosts::new);
		});
	}
}
