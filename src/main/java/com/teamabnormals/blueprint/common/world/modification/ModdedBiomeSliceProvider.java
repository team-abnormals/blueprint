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
import java.util.function.Supplier;

/**
 * A {@link DataProvider} implementation for {@link ModdedBiomeSlice} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class ModdedBiomeSliceProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private final List<Pair<ConditionedResourceSelector, ModdedBiomeSlice>> slices = new LinkedList<>();
	private final DataGenerator dataGenerator;
	private final String modid;
	private final Supplier<DynamicOps<JsonElement>> ops;

	/**
	 * Constructs a new {@link ModdedBiomeSliceProvider} instance.
	 *
	 * @param dataGenerator A {@link DataGenerator} instance to use.
	 * @param modid         The ID of the mod to provide data for.
	 * @param ops           A {@link DynamicOps} instance for serializing the {@link ModdedBiomeSliceProvider} instances.
	 */
	protected ModdedBiomeSliceProvider(DataGenerator dataGenerator, String modid, DynamicOps<JsonElement> ops) {
		this.dataGenerator = dataGenerator;
		this.modid = modid;
		this.ops = () -> ops;
	}

	/**
	 * Constructs a new {@link ModdedBiomeSliceProvider} instance where the {@link #ops} is {@link RegistryAccess#BUILTIN}.
	 *
	 * @param dataGenerator A {@link DataGenerator} instance to use.
	 * @param modid         The ID of the mod to provide data for.
	 * @see #ModdedBiomeSliceProvider(DataGenerator, String, DynamicOps)
	 */
	protected ModdedBiomeSliceProvider(DataGenerator dataGenerator, String modid) {
		this.dataGenerator = dataGenerator;
		this.modid = modid;
		this.ops = () -> RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get());
	}

	@Override
	public void run(HashCache hashCache) {
		HashSet<ResourceLocation> names = new HashSet<>();
		Path outputFolder = this.dataGenerator.getOutputFolder();
		String basePath = "data/" + this.modid + "/modded_biome_slices/";
		DynamicOps<JsonElement> ops = this.ops.get();
		var slices = this.slices;
		slices.clear();
		this.registerSlices();
		slices.forEach(pair -> {
			var slice = pair.getSecond();
			ResourceLocation name = slice.name();
			if (!names.add(name)) {
				throw new IllegalStateException("Duplicate modded biome slice: " + name);
			} else {
				Path path = outputFolder.resolve(basePath + name.getPath() + ".json");
				try {
					DataProvider.save(GSON, hashCache, slice.serializeWithSelector(pair.getFirst(), ops), path);
				} catch (JsonParseException | IOException exception) {
					LOGGER.error("Couldn't save modded biome slice {}", path, exception);
				}
			}
		});
	}

	/**
	 * Override this method to register your slices.
	 */
	protected abstract void registerSlices();

	/**
	 * Registers a {@link ModdedBiomeSlice} instance to get generated.
	 *
	 * @param name     A name for the slice.
	 * @param selector A {@link ConditionedResourceSelector} instance to use for selecting which levels the slice will get added to.
	 * @param weight   The weight of the slice.
	 * @param provider A {@link BiomeUtil.ModdedBiomeProvider} instance to use in the slice.
	 */
	protected void registerSlice(String name, ConditionedResourceSelector selector, int weight, BiomeUtil.ModdedBiomeProvider provider) {
		this.slices.add(Pair.of(selector, new ModdedBiomeSlice(new ResourceLocation(this.modid, name), weight, provider)));
	}

	/**
	 * Registers a {@link ModdedBiomeSlice} instance with no conditions to get generated.
	 *
	 * @param name     A name for the slice.
	 * @param weight   The weight of the slice.
	 * @param provider A {@link BiomeUtil.ModdedBiomeProvider} instance to use in the slice.
	 * @param levels   An array of dimensions the slice will get added to.
	 */
	protected final void registerSlice(String name, int weight, BiomeUtil.ModdedBiomeProvider provider, ResourceLocation... levels) {
		this.registerSlice(name, new ConditionedResourceSelector(new NamesResourceSelector(levels)), weight, provider);
	}

	@Override
	public String getName() {
		return "Modded Biome Slices: " + this.modid;
	}
}
