package com.teamabnormals.blueprint.common.world.modification.chunk;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.common.world.biome.modification.BiomeModificationManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.ConfiguredModifier;
import com.teamabnormals.blueprint.core.util.modification.TargetedModifier;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The data manager class for the {@link com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.IChunkGeneratorModifier} system.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class ChunkGeneratorModificationManager extends SimpleJsonResourceReloadListener {
	private static ChunkGeneratorModificationManager INSTANCE;
	private final RegistryReadOps<JsonElement> readOps;
	private final EnumMap<EventPriority, LinkedList<TargetedModifier<ChunkGenerator, RegistryWriteOps<JsonElement>, RegistryReadOps<JsonElement>>>> modifiers = new EnumMap<>(EventPriority.class);

	public ChunkGeneratorModificationManager(RegistryReadOps<JsonElement> readOps) {
		super(new Gson(), "modifiers/dimension/chunk_generator");
		this.readOps = readOps;
	}

	static {
		for (EventPriority priority : EventPriority.values()) {
			MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> {
				if (INSTANCE == null) return;
				var modifiers = INSTANCE.modifiers;
				if (modifiers == null) return;
				var targetedModifiers = modifiers.get(priority);
				if (targetedModifiers == null) return;
				//Because dimensions don't exist till the server is ready to start, we must gather target names right before the server starts
				var dimensions = event.getServer().getWorldData().worldGenSettings().dimensions();
				var keySet = dimensions.keySet();
				HashMap<ResourceLocation, LinkedList<ConfiguredModifier<ChunkGenerator, ?, RegistryWriteOps<JsonElement>, RegistryReadOps<JsonElement>, ?>>> map = new HashMap<>();
				SelectionSpace selectionSpace = (consumer) -> keySet.forEach(location -> consumer.accept(location, null));
				for (var targetedModifier : targetedModifiers) {
					targetedModifier.getTargetSelector().getTargetNames(selectionSpace).forEach(location -> {
						map.computeIfAbsent(location, __ -> new LinkedList<>()).addAll(targetedModifier.getConfiguredModifiers());
					});
				}
				for (var entry : dimensions.entrySet()) {
					var configuredModifiers = map.get(entry.getKey().location());
					if (configuredModifiers == null) continue;
					ChunkGenerator chunkGenerator = entry.getValue().generator();
					configuredModifiers.forEach(configured -> configured.modify(chunkGenerator));
				}
			});
		}
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) {
		try {
			RegistryAccess registryAccess = (RegistryAccess) BiomeModificationManager.REGISTRY_ACCESS.get(BiomeModificationManager.TAG_MANAGER.get(event.getDataPackRegistries()));
			RegistryReadOps<JsonElement> readOps = BiomeModificationManager.getReadOps(registryAccess);
			if (readOps != null) {
				event.addListener(INSTANCE = new ChunkGeneratorModificationManager(readOps));
			} else {
				Blueprint.LOGGER.error("Failed to get RegistryReadOps for the ChunkGeneratorModificationManager for an unknown RegistryAccess: " + registryAccess);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		RegistryReadOps<JsonElement> readOps = this.readOps;
		int loadedModifiers = 0;
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			try {
				TargetedModifier<ChunkGenerator, RegistryWriteOps<JsonElement>, RegistryReadOps<JsonElement>> targetedModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), readOps, ChunkGeneratorModifiers.REGISTRY);
				this.modifiers.computeIfAbsent(targetedModifier.getPriority(), __ -> new LinkedList<>()).add(targetedModifier);
				loadedModifiers++;
			} catch (IllegalArgumentException | JsonParseException exception) {
				Blueprint.LOGGER.error("Parsing error loading Chunk Generator Modifier: {}", entry.getKey(), exception);
			}
		}
		Blueprint.LOGGER.info("Chunk Generator Modification Manager has loaded {} modifiers", loadedModifiers);
	}
}
