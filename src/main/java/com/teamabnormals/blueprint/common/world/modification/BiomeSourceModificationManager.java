package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The data manager class for {@link BiomeSourceModifier} instances.
 * <p>This class handles the deserializing and applying of {@link BiomeSourceModifier} instances.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class BiomeSourceModificationManager extends SimpleJsonResourceReloadListener {
	private static final Field NOISE_GENERATOR_SETTINGS = ObfuscationReflectionHelper.findField(NoiseBasedChunkGenerator.class, "f_64318_");
	private static BiomeSourceModificationManager INSTANCE;
	private final List<BiomeSourceModifier> modifiers = new LinkedList<>();
	private final RegistryOps<JsonElement> registryOps;

	public BiomeSourceModificationManager(RegistryOps<JsonElement> registryOps) {
		super(new Gson(), "modifiers/dimension/biome_sources");
		this.registryOps = registryOps;
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) {
		try {
			event.addListener(INSTANCE = new BiomeSourceModificationManager(DataUtil.createRegistryOps(event.getServerResources())));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void onServerAboutToStart(ServerAboutToStartEvent event) {
		if (INSTANCE == null) return;
		List<BiomeSourceModifier> modifiers = INSTANCE.modifiers;
		if (modifiers.isEmpty()) return;
		MinecraftServer server = event.getServer();
		WorldGenSettings worldGenSettings = server.getWorldData().worldGenSettings();
		var dimensions = worldGenSettings.dimensions();
		var keySet = dimensions.keySet();
		SelectionSpace selectionSpace = (consumer) -> keySet.forEach(location -> consumer.accept(location, null));
		HashMap<ResourceLocation, ArrayList<BiomeUtil.ModdedBiomeProvider>> map = new HashMap<>();
		for (BiomeSourceModifier modifier : modifiers) {
			BiomeUtil.ModdedBiomeProvider provider = modifier.provider();
			if (provider.getWeight() <= 0) return;
			modifier.targetSelector().getTargetNames(selectionSpace).forEach(location -> {
				map.computeIfAbsent(location, __ -> new ArrayList<>()).add(provider);
			});
		}
		long seed = worldGenSettings.seed();
		RegistryAccess registryAccess = server.registryAccess();
		Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
		Registry<NormalNoise.NoiseParameters> noiseParametersRegistry = registryAccess.registryOrThrow(Registry.NOISE_REGISTRY);
		Registry<DensityFunction> densityFunctionRegistry = registryAccess.registryOrThrow(Registry.DENSITY_FUNCTION_REGISTRY);
		NoiseSettings defaultNoiseSettings = registryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(NoiseGeneratorSettings.OVERWORLD).noiseSettings();
		DensityFunction defaultModdedness = densityFunctionRegistry.getOrThrow(ModdedBiomeSource.DEFAULT_MODDEDNESS);
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
			ResourceLocation location = entry.getKey().location();
			ArrayList<BiomeUtil.ModdedBiomeProvider> providersForKey = map.get(location);
			if (providersForKey != null && !providersForKey.isEmpty()) {
				ChunkGenerator chunkGenerator = entry.getValue().generator();
				BiomeSource source = chunkGenerator.getBiomeSource();
				//Checking specifically for an instance of MultiNoiseBiomeSource isn't reliable because mods may alter the biome source before we do
				//If we do replace something we shouldn't then players can remove providers in a datapack
				//TODO: Mostly experimental! Works with Terralith, Biomes O' Plenty, and more, but still needs more testing!
				if (!(source instanceof FixedBiomeSource) && !(source instanceof CheckerboardColumnBiomeSource)) {
					boolean legacy = false;
					boolean noiseBased = chunkGenerator instanceof NoiseBasedChunkGenerator;
					NoiseSettings noiseSettings = defaultNoiseSettings;
					if (noiseBased) {
						try {
							NoiseGeneratorSettings settings = ((Holder<NoiseGeneratorSettings>) NOISE_GENERATOR_SETTINGS.get(chunkGenerator)).value();
							if (settings != null) {
								legacy = settings.useLegacyRandomSource();
								noiseSettings = settings.noiseSettings();
							}
						} catch (IllegalAccessException ignored) {
						}
					}
					DensityFunction moddedness = densityFunctionRegistry.get(new ResourceLocation(Blueprint.MOD_ID, "moddedness/" + location.getNamespace() + "/" + location.getPath()));
					ModdedBiomeSource moddedBiomeSource = new ModdedBiomeSource(biomeRegistry, noiseParametersRegistry, densityFunctionRegistry, source, noiseSettings, seed, legacy, moddedness != null ? moddedness : defaultModdedness, new ModdedBiomeSource.WeightedBiomeSlices(providersForKey.toArray(new BiomeUtil.ModdedBiomeProvider[0])));
					chunkGenerator.biomeSource = moddedBiomeSource;
					chunkGenerator.runtimeBiomeSource = moddedBiomeSource;
					if (noiseBased)
						((ModdedSurfaceSystem) ((NoiseBasedChunkGenerator) chunkGenerator).surfaceSystem).setModdedBiomeSource(moddedBiomeSource);
				}
			}
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		List<BiomeSourceModifier> modifiers = this.modifiers;
		modifiers.clear();
		RegistryOps<JsonElement> registryOps = this.registryOps;
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation name = entry.getKey();
			try {
				modifiers.add(BiomeSourceModifier.deserialize(name, entry.getValue(), registryOps));
			} catch (JsonParseException exception) {
				Blueprint.LOGGER.error("Parsing error loading Biome Source Modifier: {}", name, exception);
			}
		}
		Blueprint.LOGGER.info("Biome Source Modification Manager has loaded {} modifiers", modifiers.size());
	}
}
