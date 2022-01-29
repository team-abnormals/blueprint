package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.LevelStem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link DataProvider} implementation for {@link BiomeSourceModifier} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class BiomeSourceModifierProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private final List<Pair<ResourceLocation, Function<DynamicOps<JsonElement>, Optional<JsonElement>>>> biomeSourceModifiers = new LinkedList<>();
	private final DataGenerator dataGenerator;
	private final String modid;
	private final DynamicOps<JsonElement> ops;

	/**
	 * Constructs a new {@link BiomeSourceModifierProvider} instance.
	 *
	 * @param dataGenerator A {@link DataGenerator} instance to use.
	 * @param modid         The ID of the mod to provide data for.
	 * @param ops           A {@link DynamicOps} instance for serializing the {@link BiomeSourceModifierProvider} instances.
	 */
	protected BiomeSourceModifierProvider(DataGenerator dataGenerator, String modid, DynamicOps<JsonElement> ops) {
		this.dataGenerator = dataGenerator;
		this.modid = modid;
		this.ops = ops;
	}

	/**
	 * Constructs a new {@link BiomeSourceModifierProvider} instance where the {@link #ops} is {@link RegistryAccess#builtin()}.
	 *
	 * @param dataGenerator A {@link DataGenerator} instance to use.
	 * @param modid         The ID of the mod to provide data for.
	 * @see #BiomeSourceModifierProvider(DataGenerator, String, DynamicOps)
	 */
	protected BiomeSourceModifierProvider(DataGenerator dataGenerator, String modid) {
		this(dataGenerator, modid, RegistryWriteOps.create(JsonOps.INSTANCE, RegistryAccess.builtin()));
	}

	@Override
	public void run(HashCache hashCache) {
		HashSet<ResourceLocation> names = new HashSet<>();
		Path outputFolder = this.dataGenerator.getOutputFolder();
		String basePath = "data/" + this.modid + "/modifiers/dimension/biome_sources/";
		DynamicOps<JsonElement> ops = this.ops;
		var biomeSourceModifiers = this.biomeSourceModifiers;
		biomeSourceModifiers.clear();
		this.registerModifiers();
		biomeSourceModifiers.forEach(pair -> {
			ResourceLocation name = pair.getFirst();
			if (!names.add(name)) {
				throw new IllegalStateException("Duplicate biome source modifier: " + name);
			} else {
				Path path = outputFolder.resolve(basePath + name.getPath() + ".json");
				try {
					var result = pair.getSecond().apply(ops);
					if (result.isPresent()) {
						DataProvider.save(GSON, hashCache, result.get(), path);
					} else LOGGER.error("Couldn't serialize biome source modifier {}", path);
				} catch (IOException exception) {
					LOGGER.error("Couldn't save biome source modifier {}", path, exception);
				}
			}
		});
	}

	/**
	 * Override this method to register your modifiers.
	 */
	protected abstract void registerModifiers();

	/**
	 * Registers a {@link BiomeSourceModifier} instance to be generated.
	 *
	 * @param name     The name of the {@link BiomeSourceModifier} instance.
	 * @param modifier A {@link BiomeSourceModifier} instance to be generated.
	 */
	protected void registerModifier(String name, BiomeSourceModifier modifier) {
		this.biomeSourceModifiers.add(Pair.of(new ResourceLocation(this.modid, name), (ops) -> BiomeSourceModifier.CODEC.encodeStart(ops, modifier).result()));
	}

	/**
	 * Registers a {@link BiomeUtil.ModdedBiomeProvider} instance to be generated.
	 *
	 * @param name     The name of the provider.
	 * @param provider A {@link BiomeUtil.ModdedBiomeProvider} instance to be generated.
	 * @param targets  An array of {@link LevelStem} target keys.
	 */
	@SafeVarargs
	protected final void registerModifier(String name, BiomeUtil.ModdedBiomeProvider provider, ResourceKey<LevelStem>... targets) {
		this.registerModifier(name, new BiomeSourceModifier(List.of(targets), provider));
	}

	/**
	 * Registers a {@link BiomeUtil.MultiNoiseModdedBiomeProvider} instance to be generated.
	 * <p>This method is unique because it eases generation of multi noise providers by using biome resource keys instead of biome suppliers.</p>
	 *
	 * @param name    The name of the provider.
	 * @param biomes  A list of pairs containing a {@link Climate.ParameterPoint} instance and a biome {@link ResourceKey}.
	 * @param weight  The weight of the provider.
	 * @param targets An array of {@link LevelStem} target keys.
	 */
	@SafeVarargs
	protected final void registerMultiNoiseProvider(String name, List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> biomes, int weight, ResourceKey<LevelStem>... targets) {
		ResourceLocation location = new ResourceLocation(this.modid, name);
		this.biomeSourceModifiers.add(Pair.of(location, (ops) -> {
			JsonObject object = new JsonObject();
			JsonArray targetsArray = new JsonArray();
			for (var key : targets) targetsArray.add(key.location().toString());
			object.add("targets", targetsArray);
			JsonObject provider = new JsonObject();
			provider.addProperty("name", location.toString());
			JsonArray biomesArray = new JsonArray();
			for (var pair : biomes) {
				JsonObject entry = new JsonObject();
				Optional<JsonElement> parameters = Climate.ParameterPoint.CODEC.encodeStart(ops, pair.getFirst()).result();
				if (parameters.isEmpty()) return Optional.empty();
				entry.add("parameters", parameters.get());
				entry.addProperty("biome", pair.getSecond().location().toString());
				biomesArray.add(entry);
			}
			provider.add("biomes", biomesArray);
			provider.addProperty("weight", weight);
			provider.addProperty("type", "blueprint:multi_noise");
			object.add("provider", provider);
			return Optional.of(object);
		}));
	}

	@Override
	public String getName() {
		return "Biome Source Modifiers: " + this.modid;
	}
}
