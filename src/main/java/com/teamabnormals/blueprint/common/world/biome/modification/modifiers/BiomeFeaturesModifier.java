package com.teamabnormals.blueprint.common.world.biome.modification.modifiers;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An {@link IBiomeModifier} implementation that modifies the features of a {@link BiomeLoadingEvent} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeFeaturesModifier implements IBiomeModifier<BiomeFeaturesModifier.Config> {
	private static final Field FEATURES = ObfuscationReflectionHelper.findField(BiomeGenerationSettings.Builder.class, "f_47828_");
	private static final Map<String, GenerationStep.Decoration> DECORATION_NAME_MAP = Stream.of(GenerationStep.Decoration.values()).collect(Collectors.toMap(decoration -> decoration.name().toLowerCase(), decoration -> decoration));
	private static final Codec<List<List<Supplier<PlacedFeature>>>> PLACED_FEATURES_CODEC = PlacedFeature.LIST_CODEC.promotePartial(Util.prefix("Feature: ", Blueprint.LOGGER::error)).flatXmap(ExtraCodecs.nonNullSupplierListCheck(), ExtraCodecs.nonNullSupplierListCheck()).listOf();

	private static boolean testPlacements(List<PlacementModifierType<?>> types, List<PlacementModifier> placements) {
		int size = types.size();
		if (size == 0) return true;
		if (size != placements.size()) return false;
		boolean matchesTypes = true;
		for (int i = 0; i < size; i++) {
			if (placements.get(i).type() != types.get(i)) {
				matchesTypes = false;
				break;
			}
		}
		return matchesTypes;
	}

	//TODO: Rework this to allow for more freedom and less verbose definitions
	private static boolean testConfiguredFeatures(List<JsonElement> configuredFeatures, List<JsonElement> targets) {
		return configuredFeatures.equals(targets);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modify(BiomeLoadingEvent event, Config config) {
		try {
			BiomeGenerationSettingsBuilder builder = event.getGeneration();
			//Copy to account for the features possibly being immutable
			List<List<Supplier<PlacedFeature>>> features = new ArrayList<>((List<List<Supplier<PlacedFeature>>>) FEATURES.get(builder));
			int featuresSize = features.size();
			config.replace.ifPresent(map -> {
				int featuresSizeMinusOne = features.size() - 1;
				map.forEach((decoration, suppliers) -> {
					int ordinal = decoration.ordinal();
					if (ordinal > featuresSizeMinusOne) {
						features.add(suppliers);
					} else {
						features.set(ordinal, suppliers);
					}
				});
			});
			config.substitute.ifPresent(lists -> {
				int listsSize = lists.size();
				for (int i = 0; i < featuresSize && i < listsSize; i++) {
					var substitutes = lists.get(i);
					if (substitutes.isEmpty()) continue;
					//Copy so the modifier can be reused
					substitutes = new ArrayList<>(substitutes);
					var mutableStepFeatures = new ArrayList<>(features.get(i));
					Iterator<Supplier<PlacedFeature>> iterator = mutableStepFeatures.iterator();
					List<Supplier<PlacedFeature>> toAdd = new ArrayList<>();
					while (iterator.hasNext()) {
						PlacedFeature placedFeature = iterator.next().get();
						var placements = placedFeature.getPlacement();
						var configuredFeatures = placedFeature.getFeatures().map(configuredFeature -> {
							return ConfiguredFeature.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, configuredFeature).result().orElse(JsonNull.INSTANCE);
						}).collect(Collectors.toList());
						var substitutesIterator = substitutes.iterator();
						while (substitutesIterator.hasNext()) {
							var nextPair = substitutesIterator.next();
							SubstituteValue value = nextPair.getSecond();
							if (testPlacements(value.placements, placements)) {
								if (testConfiguredFeatures(configuredFeatures, nextPair.getFirst())) {
									substitutesIterator.remove();
									iterator.remove();
									value.feature.ifPresent(toAdd::add);
									break;
								}
							}
						}
					}
					mutableStepFeatures.addAll(toAdd);
					features.set(i, mutableStepFeatures);
				}
			});
			config.add.ifPresent(lists -> {
				int size = lists.size();
				for (int i = 0; i < size; i++) {
					if (i >= featuresSize) {
						features.add(lists.get(i));
					} else {
						var toAdd = lists.get(i);
						if (toAdd.isEmpty()) continue;
						var mutableStepFeatures = new ArrayList<>(features.get(i));
						mutableStepFeatures.addAll(toAdd);
						features.set(i, mutableStepFeatures);
					}
				}
			});
			FEATURES.set(builder, features);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement serialize(Config config, RegistryWriteOps<JsonElement> additional) throws JsonParseException {
		JsonObject object = new JsonObject();
		var replace = config.replace;
		if (replace.isPresent()) {
			JsonObject replaceObject = new JsonObject();
			var map = replace.get();
			for (var entry : map.entrySet()) {
				var result = PlacedFeature.LIST_CODEC.encodeStart(additional, entry.getValue());
				var error = result.error();
				if (error.isPresent()) throw new JsonParseException(error.get().message());
				replaceObject.add(entry.getKey().name().toLowerCase(), result.result().get());
			}
			object.add("replace", replaceObject);
		}
		var substitute = config.substitute;
		if (substitute.isPresent()) {
			var list = substitute.get();
			JsonArray array = new JsonArray();
			for (var substitutes : list) {
				JsonArray substitutesArray = new JsonArray();
				for (var entry : substitutes) {
					JsonArray targetsArray = new JsonArray();
					for (JsonElement targetElement : entry.getFirst()) {
						targetsArray.add(targetElement);
					}
					JsonObject entryObject = new JsonObject();
					entryObject.add("targets", targetsArray);
					var replacerResult = SubstituteValue.CODEC.encodeStart(additional, entry.getSecond());
					var replacerError = replacerResult.error();
					if (replacerError.isPresent()) throw new JsonParseException(replacerError.get().message());
					entryObject.add("replacer", replacerResult.result().get());
					substitutesArray.add(entryObject);
				}
				array.add(substitutesArray);
			}
			object.add("substitute", array);
		}
		var add = config.add;
		if (add.isPresent()) {
			var result = PLACED_FEATURES_CODEC.encodeStart(additional, add.get());
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			object.add("add", result.result().get());
		}
		return object;
	}

	@Override
	public Config deserialize(JsonElement element, RegistryReadOps<JsonElement> additional) throws JsonParseException {
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			JsonElement replaceElement = object.get("replace");
			boolean replaces = replaceElement != null;
			JsonElement substituteElement = object.get("substitute");
			boolean substitutes = substituteElement != null;
			JsonElement addElement = object.get("add");
			boolean adds = addElement != null;
			if (!replaces && !substitutes && !adds) throw new JsonParseException(element + " doesn't replace, substitute, or add!");
			Optional<Map<GenerationStep.Decoration, List<Supplier<PlacedFeature>>>> replace = Optional.empty();
			if (replaces) {
				if (!replaceElement.isJsonObject()) throw new JsonParseException("Expected 'replace' to be a JsonObject");
				var entries = replaceElement.getAsJsonObject().entrySet();
				Map<GenerationStep.Decoration, List<Supplier<PlacedFeature>>> replaceMap = new EnumMap<>(GenerationStep.Decoration.class);
				for (var entry : entries) {
					String key = entry.getKey();
					GenerationStep.Decoration decoration = DECORATION_NAME_MAP.get(key);
					if (decoration == null) throw new JsonParseException("Unknown Decoration Type: " + key);
					var result = PlacedFeature.LIST_CODEC.decode(additional, entry.getValue());
					var error = result.error();
					if (error.isPresent()) throw new JsonParseException(error.get().message());
					replaceMap.put(decoration, result.result().get().getFirst());
				}
				replace = Optional.of(replaceMap);
			}
			Optional<List<List<Pair<List<JsonElement>, SubstituteValue>>>> substitute = Optional.empty();
			if (substitutes) {
				JsonArray array = GsonHelper.convertToJsonArray(substituteElement, "substitute");
				List<List<Pair<List<JsonElement>, SubstituteValue>>> substituteList = new ArrayList<>();
				for (JsonElement entry : array) {
					if (!entry.isJsonArray()) throw new JsonParseException("Expected " + entry + " to be a JsonArray");
					JsonArray mapEntries = entry.getAsJsonArray();
					List<Pair<List<JsonElement>, SubstituteValue>> substituteValues = new ArrayList<>();
					for (JsonElement mapElement : mapEntries) {
						if (!mapElement.isJsonObject()) throw new JsonParseException("Expected " + mapElement + " to be a JsonObject");
						JsonObject mapObject = mapElement.getAsJsonObject();
						JsonArray targetsArray = GsonHelper.getAsJsonArray(mapObject, "targets");
						List<JsonElement> targets = new ArrayList<>();
						targetsArray.forEach(targets::add);
						JsonElement replacerElement = mapObject.get("replacer");
						if (replacerElement == null) throw new JsonParseException("Missing 'replacer' in " + mapObject);
						var replacerResult = SubstituteValue.CODEC.decode(additional, replacerElement);
						var replacerError = replacerResult.error();
						if (replacerError.isPresent()) throw new JsonParseException(replacerError.get().message());
						substituteValues.add(Pair.of(targets, replacerResult.result().get().getFirst()));
					}
					substituteList.add(substituteValues);
				}
				substitute = Optional.of(substituteList);
			}
			Optional<List<List<Supplier<PlacedFeature>>>> add = Optional.empty();
			if (adds) {
				var result = PLACED_FEATURES_CODEC.decode(additional, addElement);
				var error = result.error();
				if (error.isPresent()) throw new JsonParseException(error.get().message());
				add = Optional.of(result.result().get().getFirst());
			}
			return new Config(replace, substitute, add);
		} else {
			throw new JsonParseException("Expected " + element + " to be a JsonObject");
		}
	}

	public static record Config(Optional<Map<GenerationStep.Decoration, List<Supplier<PlacedFeature>>>> replace, Optional<List<List<Pair<List<JsonElement>, SubstituteValue>>>> substitute, Optional<List<List<Supplier<PlacedFeature>>>> add) {
	}

	public static record SubstituteValue(Optional<Supplier<PlacedFeature>> feature, List<PlacementModifierType<?>> placements) {
		public static final Codec<SubstituteValue> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					PlacedFeature.CODEC.optionalFieldOf("feature").forGetter(value -> value.feature),
					Registry.PLACEMENT_MODIFIERS.byNameCodec().listOf().fieldOf("placements").forGetter(value -> value.placements)
			).apply(instance, SubstituteValue::new);
		});
	}
}
