package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An {@link IBiomeModifier} implementation that modifies the spawn costs of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeSpawnCostsModifier implements IBiomeModifier<BiomeSpawnCostsModifier.Config> {
	public static final Field MOB_SPAWN_COSTS = ObfuscationReflectionHelper.findField(MobSpawnSettings.Builder.class, "f_48363_");
	@SuppressWarnings("deprecation")
	private static final Codec<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>> SPAWN_COST_MAP_CODEC = Codec.simpleMap(Registry.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, StringRepresentable.keys(Registry.ENTITY_TYPE.keySet().stream().map(BiomeSpawnersModifier::resourceLocationRepresentable).toArray(StringRepresentable[]::new))).codec();
	@SuppressWarnings("deprecation")
	private static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Registry.ENTITY_TYPE.byNameCodec().listOf().optionalFieldOf("remove").forGetter(config -> config.remove),
				SPAWN_COST_MAP_CODEC.optionalFieldOf("replace").forGetter(config -> config.replace),
				SPAWN_COST_MAP_CODEC.optionalFieldOf("add").forGetter(config -> config.add)
		).apply(instance, Config::new);
	});

	@SuppressWarnings("unchecked")
	@Override
	public void modify(BiomeLoadingEvent event, Config config) {
		try {
			Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> spawnCosts = (Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>) MOB_SPAWN_COSTS.get(event.getSpawns());
			config.remove.ifPresent(list -> list.forEach(spawnCosts::remove));
			config.replace.ifPresent(spawnCosts::putAll);
			config.add.ifPresent(map -> {
				map.forEach((type, mobSpawnCost) -> {
					if (!spawnCosts.containsKey(type)) {
						spawnCosts.put(type, mobSpawnCost);
					}
				});
			});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement serialize(Config config, RegistryWriteOps<JsonElement> additional) throws JsonParseException {
		var result = CODEC.encodeStart(additional, config);
		var error = result.error();
		if (error.isPresent()) {
			throw new JsonParseException(error.get().message());
		}
		return result.result().get();
	}

	@Override
	public Config deserialize(JsonElement element, RegistryReadOps<JsonElement> additional) throws JsonParseException {
		var result = CODEC.decode(additional, element);
		var error = result.error();
		if (error.isPresent()) {
			throw new JsonParseException(error.get().message());
		}
		return result.result().get().getFirst();
	}

	public static record Config(Optional<List<EntityType<?>>> remove, Optional<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>> replace, Optional<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>> add) {
	}
}
