package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.ErrorableOptionalFieldCodec;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.*;

/**
 * An {@link IBiomeModifier} implementation that modifies the spawners of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeSpawnersModifier implements IBiomeModifier<BiomeSpawnersModifier.Config> {
	@SuppressWarnings("deprecation")
	private static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				new ErrorableOptionalFieldCodec<>("remove", Codec.simpleMap(MobCategory.CODEC, Registry.ENTITY_TYPE.byNameCodec().listOf().xmap(HashSet::new, ArrayList::new), StringRepresentable.keys(MobCategory.values())).codec()).forGetter(config -> config.remove),
				new ErrorableOptionalFieldCodec<>("replace", Codec.simpleMap(MobCategory.CODEC, Codec.simpleMap(Registry.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.SpawnerData.CODEC, StringRepresentable.keys(Registry.ENTITY_TYPE.keySet().stream().map(BiomeSpawnersModifier::resourceLocationRepresentable).toArray(StringRepresentable[]::new))).codec(), StringRepresentable.keys(MobCategory.values())).codec()).forGetter(config -> config.replace),
				new ErrorableOptionalFieldCodec<>("add", Codec.simpleMap(MobCategory.CODEC, MobSpawnSettings.SpawnerData.CODEC.listOf().promotePartial(Util.prefix("Spawn data: ", Blueprint.LOGGER::error)), StringRepresentable.keys(MobCategory.values())).codec()).forGetter(config -> config.add)
		).apply(instance, Config::new);
	});

	public static StringRepresentable resourceLocationRepresentable(ResourceLocation location) {
		return location::toString;
	}

	@Override
	public void modify(BiomeLoadingEvent event, Config config) {
		MobSpawnSettingsBuilder spawnSettings = event.getSpawns();
		config.remove.ifPresent(map -> {
			var spawners = spawnSettings.spawners;
			map.forEach((mobCategory, set) -> {
				spawners.get(mobCategory).removeIf(data -> set.contains(data.type));
			});
		});
		config.replace.ifPresent(map -> {
			map.forEach((mobCategory, typeMap) -> {
				List<MobSpawnSettings.SpawnerData> toReplace = new ArrayList<>();
				var categorizedSpawners = spawnSettings.spawners.get(mobCategory);
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
		config.add.ifPresent(map -> {
			map.forEach((mobCategory, list) -> {
				list.forEach(spawnerData -> {
					spawnSettings.addSpawn(mobCategory, spawnerData);
				});
			});
		});
	}

	@Override
	public JsonElement serialize(Config config, Void additional) throws JsonParseException {
		var result = CODEC.encodeStart(JsonOps.INSTANCE, config);
		var error = result.error();
		if (error.isPresent()) {
			throw new JsonParseException(error.get().message());
		}
		return result.result().get();
	}

	@Override
	public Config deserialize(JsonElement element, Void additional) throws JsonParseException {
		var result = CODEC.decode(JsonOps.INSTANCE, element);
		var error = result.error();
		if (error.isPresent()) {
			throw new JsonParseException(error.get().message());
		}
		return result.result().get().getFirst();
	}

	public static record Config(Optional<Map<MobCategory, HashSet<EntityType<?>>>> remove, Optional<Map<MobCategory, Map<EntityType<?>, MobSpawnSettings.SpawnerData>>> replace, Optional<Map<MobCategory, List<MobSpawnSettings.SpawnerData>>> add) {
	}
}
