package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link DataProvider} implementation for {@link ModdedBiomeSlice} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class ModdedBiomeSliceProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Pair<ConditionedResourceSelector, ModdedBiomeSlice>> slices = new LinkedList<>();
	private final PackOutput.PathProvider pathProvider;
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	private final String modid;

	/**
	 * Constructs a new {@link ModdedBiomeSliceProvider} instance.
	 *
	 * @param modid          The ID of the mod to provide data for.
	 * @param output         A {@link PackOutput} instance to use.
	 * @param lookupProvider A completable future {@link HolderLookup.Provider} for registry access.
	 */
	protected ModdedBiomeSliceProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		this.modid = modid;
		this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "modded_biome_slices");
		this.lookupProvider = lookupProvider;
	}

	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		return this.lookupProvider.thenCompose(provider -> {
			var slices = this.slices;
			slices.clear();
			this.registerSlices(provider);
			PackOutput.PathProvider pathProvider = this.pathProvider;
			DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);
			return CompletableFuture.completedFuture(slices.stream().map(pair -> {
				var slice = pair.getSecond();
				ResourceLocation name = slice.name();
				Path path = pathProvider.json(name);
				try {
					return DataProvider.saveStable(cachedOutput, slice.serializeWithSelector(pair.getFirst(), ops), path);
				} catch (JsonParseException exception) {
					LOGGER.error("Couldn't save modded biome slice {}", path, exception);
					return CompletableFuture.completedFuture(null);
				}
			}).toArray(CompletableFuture[]::new));
		});
	}

	/**
	 * Override this method to register your slices.
	 *
	 * @param provider A {@link HolderLookup.Provider} instance for registry access.
	 */
	protected abstract void registerSlices(HolderLookup.Provider provider);

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
