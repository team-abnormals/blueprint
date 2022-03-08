package com.teamabnormals.blueprint.common.world.modification.chunk;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.modification.ConfiguredModifier;
import com.teamabnormals.blueprint.core.util.modification.TargetedModifier;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.resources.RegistryOps;
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
	private final RegistryOps<JsonElement> registryOps;
	private final EnumMap<EventPriority, LinkedList<TargetedModifier<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>>>> modifiers = new EnumMap<>(EventPriority.class);

	public ChunkGeneratorModificationManager(RegistryOps<JsonElement> registryOps) {
		super(new Gson(), "modifiers/dimension/chunk_generator");
		this.registryOps = registryOps;
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
				HashMap<ResourceLocation, LinkedList<ConfiguredModifier<ChunkGenerator, ?, RegistryOps<JsonElement>, RegistryOps<JsonElement>, ?>>> map = new HashMap<>();
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
			event.addListener(INSTANCE = new ChunkGeneratorModificationManager(DataUtil.createRegistryOps(event.getServerResources())));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		RegistryOps<JsonElement> registryOps = this.registryOps;
		int loadedModifiers = 0;
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			try {
				TargetedModifier<ChunkGenerator, RegistryOps<JsonElement>, RegistryOps<JsonElement>> targetedModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), registryOps, ChunkGeneratorModifiers.REGISTRY);
				this.modifiers.computeIfAbsent(targetedModifier.getPriority(), __ -> new LinkedList<>()).add(targetedModifier);
				loadedModifiers++;
			} catch (IllegalArgumentException | JsonParseException exception) {
				Blueprint.LOGGER.error("Parsing error loading Chunk Generator Modifier: {}", entry.getKey(), exception);
			}
		}
		Blueprint.LOGGER.info("Chunk Generator Modification Manager has loaded {} modifiers", loadedModifiers);
	}
}
