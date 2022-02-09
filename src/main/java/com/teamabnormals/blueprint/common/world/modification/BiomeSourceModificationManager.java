package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.common.world.biome.modification.BiomeModificationManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
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
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

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
	private final RegistryReadOps<JsonElement> readOps;

	public BiomeSourceModificationManager(RegistryReadOps<JsonElement> readOps) {
		super(new Gson(), "modifiers/dimension/biome_sources");
		this.readOps = readOps;
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) {
		try {
			RegistryAccess registryAccess = (RegistryAccess) BiomeModificationManager.REGISTRY_ACCESS.get(BiomeModificationManager.TAG_MANAGER.get(event.getDataPackRegistries()));
			RegistryReadOps<JsonElement> readOps = BiomeModificationManager.getReadOps(registryAccess);
			if (readOps != null) {
				event.addListener(INSTANCE = new BiomeSourceModificationManager(readOps));
			} else {
				Blueprint.LOGGER.error("Failed to get RegistryReadOps for the BiomeSourceModificationManager for an unknown RegistryAccess: " + registryAccess);
			}
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
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
			ArrayList<BiomeUtil.ModdedBiomeProvider> providersForKey = map.get(entry.getKey().location());
			if (!providersForKey.isEmpty()) {
				ChunkGenerator chunkGenerator = entry.getValue().generator();
				BiomeSource source = chunkGenerator.getBiomeSource();
				//Checking specifically for an instance of MultiNoiseBiomeSource isn't reliable because mods may alter the biome source before we do
				//If we do replace something we shouldn't then players can remove providers in a datapack
				//TODO: Mostly experimental! Works with Terralith, Biomes O' Plenty, and more, but still needs more testing!
				if (!(source instanceof FixedBiomeSource) && !(source instanceof CheckerboardColumnBiomeSource)) {
					boolean legacy = false;
					boolean largeBiomes = false;
					boolean noiseBased = chunkGenerator instanceof NoiseBasedChunkGenerator;
					if (noiseBased) {
						try {
							NoiseGeneratorSettings settings = ((Supplier<NoiseGeneratorSettings>) NOISE_GENERATOR_SETTINGS.get(chunkGenerator)).get();
							if (settings != null) {
								legacy = settings.useLegacyRandomSource();
								largeBiomes = settings.noiseSettings().largeBiomes();
							}
						} catch (IllegalAccessException ignored) {
						}
					}
					ModdedBiomeSource moddedBiomeSource = new ModdedBiomeSource(biomeRegistry, noiseParametersRegistry, source, seed, legacy, largeBiomes, new ModdedBiomeSource.WeightedBiomeSlices(providersForKey.toArray(new BiomeUtil.ModdedBiomeProvider[0])));
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
		RegistryReadOps<JsonElement> readOps = this.readOps;
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation name = entry.getKey();
			try {
				modifiers.add(BiomeSourceModifier.deserialize(name, entry.getValue(), readOps));
			} catch (JsonParseException exception) {
				Blueprint.LOGGER.error("Parsing error loading Biome Source Modifier: {}", name, exception);
			}
		}
		Blueprint.LOGGER.info("Biome Source Modification Manager has loaded {} modifiers", modifiers.size());
	}
}
