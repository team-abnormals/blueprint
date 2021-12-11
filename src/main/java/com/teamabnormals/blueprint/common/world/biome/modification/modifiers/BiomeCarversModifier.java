package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.mixin.ConfiguredWorldCarverAccessorMixin;
import com.teamabnormals.blueprint.core.util.modification.IModifier;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

/**
 * An {@link IBiomeModifier} implementation that modifies the carvers of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeCarversModifier implements IBiomeModifier<Map<GenerationStep.Carving, BiomeCarversModifier.CarvingValue>> {
	private static final Field CARVERS = ObfuscationReflectionHelper.findField(BiomeGenerationSettings.Builder.class, "f_47827_");

	@SuppressWarnings("unchecked")
	@Override
	public void modify(BiomeLoadingEvent event, Map<GenerationStep.Carving, BiomeCarversModifier.CarvingValue> config) {
		try {
			Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers = (Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>>) CARVERS.get(event.getGeneration());
			config.forEach((carving, carvingValue) -> {
				var replace = carvingValue.replace;
				List<Supplier<ConfiguredWorldCarver<?>>> list;
				if (replace.isPresent()) {
					carvers.put(carving, list = new ArrayList<>(replace.get()));
				} else {
					list = carvers.computeIfAbsent(carving, key -> new ArrayList<>());
				}
				carvingValue.substitute.ifPresent(substitutes -> {
					var iterator = list.iterator();
					List<Supplier<ConfiguredWorldCarver<?>>> toAdd = new ArrayList<>();
					while (iterator.hasNext()) {
						ConfiguredWorldCarver<?> next = iterator.next().get();
						for (var pair : substitutes) {
							var target = pair.getFirst();
							var targetCarver = target.getFirst();
							if (targetCarver.isEmpty() || targetCarver.get() == ((ConfiguredWorldCarverAccessorMixin<?>) next).getWorldCarver()) {
								JsonElement jsonResult = ConfiguredWorldCarver.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, next).result().orElse(JsonNull.INSTANCE);
								if (jsonResult instanceof JsonObject jsonObject && IModifier.weakElementEquals(jsonObject.get("config"), target.getSecond())) {
									iterator.remove();
									pair.getSecond().ifPresent(toAdd::add);
									break;
								}
							}
						}
					}
					list.addAll(toAdd);
				});
				carvingValue.add.ifPresent(list::addAll);
			});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement serialize(Map<GenerationStep.Carving, BiomeCarversModifier.CarvingValue> config, RegistryWriteOps<JsonElement> additional) throws JsonParseException {
		JsonObject jsonObject = new JsonObject();
		config.forEach((carving, carvingValue) -> {
			JsonObject carvingValueObject = new JsonObject();
			carvingValue.replace.ifPresent(suppliers -> {
				var result = ConfiguredWorldCarver.LIST_CODEC.encodeStart(additional, suppliers);
				var error = result.error();
				if (error.isPresent()) throw new JsonParseException(error.get().message());
				carvingValueObject.add("replace", result.result().get());
			});
			carvingValue.substitute.ifPresent(list -> {
				JsonArray array = new JsonArray();
				list.forEach(entry -> {
					JsonObject entryObject = new JsonObject();
					JsonObject targetObject = new JsonObject();
					var target = entry.getFirst();
					target.getFirst().ifPresent(worldCarver -> {
						ResourceLocation carverName = worldCarver.getRegistryName();
						if (carverName == null) throw new JsonParseException("Unknown WorldCarver: " + worldCarver);
						targetObject.addProperty("carver", carverName.toString());
					});
					targetObject.add("config", target.getSecond());
					entryObject.add("target", targetObject);
					entry.getSecond().ifPresent(replacer -> {
						var result = ConfiguredWorldCarver.CODEC.encodeStart(additional, replacer);
						var error = result.error();
						if (error.isPresent()) throw new JsonParseException(error.get().message());
						entryObject.add("replacer", result.result().get());
					});
					array.add(entryObject);
				});
				carvingValueObject.add("substitute", array);
			});
			carvingValue.add.ifPresent(suppliers -> {
				var result = ConfiguredWorldCarver.LIST_CODEC.encodeStart(additional, suppliers);
				var error = result.error();
				if (error.isPresent()) throw new JsonParseException(error.get().message());
				carvingValueObject.add("add", result.result().get());
			});
			jsonObject.add(carving.getName(), carvingValueObject);
		});
		return jsonObject;
	}

	@Override
	public Map<GenerationStep.Carving, BiomeCarversModifier.CarvingValue> deserialize(JsonElement element, RegistryReadOps<JsonElement> additional) throws JsonParseException {
		if (element instanceof JsonObject jsonObject) {
			EnumMap<GenerationStep.Carving, BiomeCarversModifier.CarvingValue> map = new EnumMap<>(GenerationStep.Carving.class);
			jsonObject.entrySet().forEach(entry -> {
				String key = entry.getKey();
				GenerationStep.Carving carving = GenerationStep.Carving.byName(key);
				if (carving == null) throw new JsonParseException("Unknown Carving Step: " + key);
				JsonElement carvingValueElement = entry.getValue();
				if (carvingValueElement instanceof JsonObject carvingValueObject) {
					JsonElement replaceElement = carvingValueObject.get("replace");
					boolean replaces = replaceElement != null;
					JsonElement substituteElement = carvingValueObject.get("substitute");
					boolean substitutes = substituteElement != null;
					JsonElement addElement = carvingValueObject.get("add");
					boolean adds = addElement != null;
					if (!replaces && !substitutes && !adds) throw new JsonParseException(carvingValueElement + " doesn't replace, substitute, or add!");
					Optional<List<Supplier<ConfiguredWorldCarver<?>>>> replace = Optional.empty();
					if (replaces) {
						var replaceResult = ConfiguredWorldCarver.LIST_CODEC.decode(additional, replaceElement);
						var replaceError = replaceResult.error();
						if (replaceError.isPresent()) throw new JsonParseException(replaceError.get().message());
						replace = Optional.of(replaceResult.result().get().getFirst());
					}
					Optional<List<Pair<Pair<Optional<WorldCarver<?>>, JsonElement>, Optional<Supplier<ConfiguredWorldCarver<?>>>>>> substitute = Optional.empty();
					if (substitutes) {
						JsonArray jsonArray = GsonHelper.convertToJsonArray(substituteElement, "substitute");
						List<Pair<Pair<Optional<WorldCarver<?>>, JsonElement>, Optional<Supplier<ConfiguredWorldCarver<?>>>>> substituteLists = new ArrayList<>();
						for (JsonElement entryElement : jsonArray) {
							if (entryElement instanceof JsonObject entryObject) {
								JsonObject targetObject = GsonHelper.getAsJsonObject(entryObject, "target");
								Optional<WorldCarver<?>> carver = Optional.empty();
								JsonElement carverElement = targetObject.get("carver");
								if (carverElement instanceof JsonPrimitive primitive && primitive.isString()) {
									ResourceLocation carverName = new ResourceLocation(primitive.getAsString());
									WorldCarver<?> lookedUpCarver = ForgeRegistries.WORLD_CARVERS.getValue(carverName);
									if (lookedUpCarver == null) throw new JsonParseException("Unknown WorldCarver: " + carverName);
									carver = Optional.of(lookedUpCarver);
								} else if (carverElement != null) throw new JsonParseException("Expected " + carverElement + " to be a String");
								JsonElement config = targetObject.get("config");
								if (config == null) throw new JsonParseException("'config' missing in " + targetObject);
								Optional<Supplier<ConfiguredWorldCarver<?>>> replacer = Optional.empty();
								JsonElement replacerElement = entryObject.get("replacer");
								if (replacerElement != null) {
									var replacerResult = ConfiguredWorldCarver.CODEC.decode(additional, replacerElement);
									var replacerError = replacerResult.error();
									if (replacerError.isPresent()) throw new JsonParseException(replacerError.get().message());
									replacer = Optional.of(replacerResult.result().get().getFirst());
								}
								substituteLists.add(Pair.of(Pair.of(carver, config), replacer));
							} else throw new JsonParseException("Expected " + entryElement + " to be a JsonObject");
						}
						substitute = Optional.of(substituteLists);
					}
					Optional<List<Supplier<ConfiguredWorldCarver<?>>>> add = Optional.empty();
					if (adds) {
						var addResult = ConfiguredWorldCarver.LIST_CODEC.decode(additional, addElement);
						var addError = addResult.error();
						if (addError.isPresent()) throw new JsonParseException(addError.get().message());
						add = Optional.of(addResult.result().get().getFirst());
					}
					map.put(carving, new CarvingValue(replace, substitute, add));
				} else throw new JsonParseException("Expected " + carvingValueElement + " for " + key + " to be a JsonObject");
			});
			return map;
		}
		throw new JsonParseException("Expected " + element + " to be a JsonObject");
	}

	public static record CarvingValue(Optional<List<Supplier<ConfiguredWorldCarver<?>>>> replace, Optional<List<Pair<Pair<Optional<WorldCarver<?>>, JsonElement>, Optional<Supplier<ConfiguredWorldCarver<?>>>>>> substitute, Optional<List<Supplier<ConfiguredWorldCarver<?>>>> add) {
	}
}
