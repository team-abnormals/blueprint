package com.teamabnormals.blueprint.common.world.modification;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.common.world.biome.modification.BiomeModificationManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The data manager class for {@link BiomeSourceModifier} instances.
 * <p>This class handles the deserializing and applying of {@link BiomeSourceModifier} instances.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class BiomeSourceModificationManager extends SimpleJsonResourceReloadListener {
	private static final Field NOISE_GENERATOR_SETTINGS = ObfuscationReflectionHelper.findField(NoiseBasedChunkGenerator.class, "settings");
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
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : worldGenSettings.dimensions().entrySet()) {
			List<BiomeUtil.ModdedBiomeProvider> providersForKey = getProvidersForKey(entry.getKey(), modifiers);
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
						} catch (IllegalAccessException ignored) {}
					}
					RegistryAccess registryAccess = server.registryAccess();
					ModdedBiomeSource moddedBiomeSource = new ModdedBiomeSource(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY), registryAccess.registryOrThrow(Registry.NOISE_REGISTRY), source, worldGenSettings.seed(), legacy, largeBiomes, new ModdedBiomeSource.WeightedBiomeSlices(providersForKey.toArray(new BiomeUtil.ModdedBiomeProvider[0])));
					chunkGenerator.biomeSource = moddedBiomeSource;
					chunkGenerator.runtimeBiomeSource = moddedBiomeSource;
					if (noiseBased) ((ModdedSurfaceSystem) ((NoiseBasedChunkGenerator) chunkGenerator).surfaceSystem).setModdedBiomeSource(moddedBiomeSource);
				}
			}
		}
	}

	private static List<BiomeUtil.ModdedBiomeProvider> getProvidersForKey(ResourceKey<LevelStem> resourceKey, List<BiomeSourceModifier> modifiers) {
		List<BiomeUtil.ModdedBiomeProvider> providers = new ArrayList<>();
		for (BiomeSourceModifier sourceModifier : modifiers) {
			if (sourceModifier.targets().contains(resourceKey)) {
				BiomeUtil.ModdedBiomeProvider provider = sourceModifier.provider();
				if (provider.getWeight() > 0) providers.add(provider);
			}
		}
		return providers;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		List<BiomeSourceModifier> modifiers = this.modifiers;
		modifiers.clear();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			var dataResult = BiomeSourceModifier.CODEC.decode(this.readOps, entry.getValue());
			var result = dataResult.result();
			if (result.isPresent()) modifiers.add(result.get().getFirst());
			else
				Blueprint.LOGGER.error("Error loading Biome Source Modifier named '{}': {}", entry.getKey(), dataResult.error().get().message());
		}
		Blueprint.LOGGER.info("Biome Source Modification Manager has loaded {} modifiers", modifiers.size());
	}
}
