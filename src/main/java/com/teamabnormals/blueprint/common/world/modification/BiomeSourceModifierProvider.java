package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link DataProvider} implementation for {@link BiomeSourceModifier} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class BiomeSourceModifierProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private final List<Pair<ResourceLocation, BiomeSourceModifier>> biomeSourceModifiers = new LinkedList<>();
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
	 * Constructs a new {@link BiomeSourceModifierProvider} instance where the {@link #ops} is {@link RegistryAccess#BUILTIN}.
	 *
	 * @param dataGenerator A {@link DataGenerator} instance to use.
	 * @param modid         The ID of the mod to provide data for.
	 * @see #BiomeSourceModifierProvider(DataGenerator, String, DynamicOps)
	 */
	protected BiomeSourceModifierProvider(DataGenerator dataGenerator, String modid) {
		this(dataGenerator, modid, RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get()));
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
					DataProvider.save(GSON, hashCache, pair.getSecond().serialize(ops), path);
				} catch (JsonParseException | IOException exception) {
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
		this.biomeSourceModifiers.add(Pair.of(new ResourceLocation(this.modid, name), modifier));
	}

	/**
	 * Registers a {@link BiomeUtil.ModdedBiomeProvider} instance with no conditions to be generated.
	 *
	 * @param name     The name of the provider.
	 * @param provider A {@link BiomeUtil.ModdedBiomeProvider} instance to be generated.
	 * @param targets  An array of target keys.
	 */
	protected final void registerModifier(String name, BiomeUtil.ModdedBiomeProvider provider, ResourceLocation... targets) {
		this.registerModifier(name, new BiomeSourceModifier(new ConditionedResourceSelector(new NamesResourceSelector(targets)), provider));
	}

	@Override
	public String getName() {
		return "Biome Source Modifiers: " + this.modid;
	}
}
