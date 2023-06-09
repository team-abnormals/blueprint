package com.teamabnormals.blueprint.common.world.modification;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The data manager class for Blueprint's modded biome sources system.
 * <p>This class handles the deserializing and applying of {@link ModdedBiomeSlice} instances.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class ModdedBiomeSlicesManager extends SimpleJsonResourceReloadListener {
	private static ModdedBiomeSlicesManager INSTANCE;
	private final List<Pair<ConditionedResourceSelector, ModdedBiomeSlice>> unassignedSlices = new LinkedList<>();
	private final RegistryOps<JsonElement> registryOps;

	public ModdedBiomeSlicesManager(RegistryOps<JsonElement> registryOps) {
		super(new Gson(), "modded_biome_slices");
		this.registryOps = registryOps;
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) {
		try {
			event.addListener(INSTANCE = new ModdedBiomeSlicesManager(DataUtil.createRegistryOps(event.getServerResources())));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	// TODO: Move to high event priority Forge event
	@SuppressWarnings("deprecation")
	public static void onServerAboutToStart(MinecraftServer server) {
		if (INSTANCE == null) return;
		var unassignedSlices = INSTANCE.unassignedSlices;
		if (unassignedSlices.isEmpty()) return;

		RegistryAccess registryAccess = server.registryAccess();
		var dimensions = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
		var keySet = dimensions.keySet();
		HashMap<ResourceLocation, ArrayList<ModdedBiomeSlice>> assignedSlices = new HashMap<>();
		for (var unassignedSlice : unassignedSlices) {
			ModdedBiomeSlice slice = unassignedSlice.getSecond();
			if (slice.weight() <= 0) return;
			unassignedSlice.getFirst().select(keySet::forEach).forEach(location -> {
				assignedSlices.computeIfAbsent(location, __ -> new ArrayList<>()).add(slice);
			});
		}

		CommentedConfig moddedBiomeSliceSizes = BlueprintConfig.COMMON.moddedBiomeSliceSizes.get();
		int defaultSize = moddedBiomeSliceSizes.getIntOrElse("default", 9);
		if (defaultSize <= 0) {
			Blueprint.LOGGER.warn("Found a non-positive value for the default slice size! Slice size 9 will be used instead.");
			defaultSize = 9;
		}

		Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
		long seed = server.getWorldData().worldGenOptions().seed();
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
			ResourceLocation location = entry.getKey().location();
			var slicesForKey = assignedSlices.get(location);
			if (slicesForKey != null && !slicesForKey.isEmpty()) {
				ChunkGenerator chunkGenerator = entry.getValue().generator();
				BiomeSource source = chunkGenerator.getBiomeSource();
				// Checking specifically for an instance of MultiNoiseBiomeSource isn't reliable because mods may alter the biome source before we do
				// If we do replace something we shouldn't then players can remove providers in a datapack
				if (!(source instanceof FixedBiomeSource) && !(source instanceof CheckerboardColumnBiomeSource)) {
					int size = moddedBiomeSliceSizes.getIntOrElse(location.toString(), defaultSize);
					if (size <= 0) size = defaultSize;
					ModdedBiomeSource moddedBiomeSource = new ModdedBiomeSource(biomeRegistry, source, slicesForKey, size, seed, location.hashCode());
					chunkGenerator.biomeSource = moddedBiomeSource;
					chunkGenerator.featuresPerStep = Suppliers.memoize(() -> {
						return FeatureSorter.buildFeaturesPerStep(List.copyOf(moddedBiomeSource.possibleBiomes()), (biomeHolder) -> {
							return chunkGenerator.getBiomeGenerationSettings(biomeHolder).features();
						}, true);
					});
					if (chunkGenerator instanceof NoiseBasedChunkGenerator)
						((HasModdedBiomeSource) chunkGenerator).setModdedBiomeSource(moddedBiomeSource);
				}
			}
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		var unassignedSlices = this.unassignedSlices;
		unassignedSlices.clear();
		RegistryOps<JsonElement> registryOps = this.registryOps;
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation name = entry.getKey();
			try {
				unassignedSlices.add(ModdedBiomeSlice.deserializeWithSelector(name, entry.getValue(), registryOps));
			} catch (JsonParseException exception) {
				Blueprint.LOGGER.error("Parsing error loading Modded Biome Slice: {}", name, exception);
			}
		}
		Blueprint.LOGGER.info("Modded Biome Slice Manager has loaded {} slices", unassignedSlices.size());
	}
}
