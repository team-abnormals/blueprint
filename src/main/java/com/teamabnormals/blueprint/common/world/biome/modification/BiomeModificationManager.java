package com.teamabnormals.blueprint.common.world.biome.modification;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.ModificationManager;
import com.teamabnormals.blueprint.core.util.modification.TargetedModifier;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Data manager class for the {@link com.teamabnormals.blueprint.common.world.biome.modification.modifiers.IBiomeModifier} system.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class BiomeModificationManager extends ModificationManager<BiomeLoadingEvent, RegistryWriteOps<JsonElement>, RegistryReadOps<JsonElement>> {
	private static final Gson GSON = new Gson();
	private static final Field TAG_MANAGER = ObfuscationReflectionHelper.findField(ServerResources.class, "f_136148_");
	private static final Field REGISTRY_ACCESS = ObfuscationReflectionHelper.findField(TagManager.class, "f_144569_");
	private static final IdentityHashMap<RegistryAccess, RegistryReadOps<JsonElement>> READ_OPS_MAP = new IdentityHashMap<>();
	private static BiomeModificationManager INSTANCE = null;

	static {
		for (EventPriority priority : EventPriority.values()) {
			MinecraftForge.EVENT_BUS.addListener(priority, (BiomeLoadingEvent event) -> {
				if (INSTANCE != null) {
					ResourceLocation name = event.getName();
					if (name != null) {
						var modifiers = INSTANCE.getModifiers(name, priority);
						if (modifiers != null) {
							modifiers.forEach(configured -> configured.modify(event));
						}
					}
				}
			});
		}
	}

	private final RegistryReadOps<JsonElement> readOps;

	private BiomeModificationManager(RegistryReadOps<JsonElement> readOps) {
		super(GSON, "modifiers/worldgen/biome");
		this.readOps = readOps;
		//TODO: Add support for more selection options, such as biome dictionary.
		this.setUnmodifiedEntries(consumer -> ForgeRegistries.BIOMES.getKeys().forEach(location -> consumer.accept(location, null)));
	}

	/**
	 * Gets the instance of the {@link BiomeModificationManager}.
	 * <p>This is initialized once it has been added as a reload listener.</p>
	 *
	 * @return The instance of the {@link BiomeModificationManager}.
	 */
	public static BiomeModificationManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Puts a {@link RegistryAccess} instance and a {@link RegistryReadOps} instance onto the {@link #READ_OPS_MAP} map.
	 * <p><b>This method is for internal use only!</b>></p>
	 *
	 * @param registryAccess A {@link RegistryAccess} instance to use for tracking the {@link RegistryReadOps} instance.
	 * @param readOps        A {@link RegistryReadOps} instance to track.
	 */
	public static void trackReadOps(RegistryAccess registryAccess, RegistryReadOps<JsonElement> readOps) {
		READ_OPS_MAP.put(registryAccess, readOps);
	}

	public static void onReloadListener(AddReloadListenerEvent event) {
		try {
			RegistryAccess registryAccess = (RegistryAccess) REGISTRY_ACCESS.get(TAG_MANAGER.get(event.getDataPackRegistries()));
			RegistryReadOps<JsonElement> readOps = READ_OPS_MAP.get(registryAccess);
			if (readOps != null) {
				event.addListener(INSTANCE = new BiomeModificationManager(readOps));
			} else {
				Blueprint.LOGGER.error("Failed to get RegistryReadOps for the BiomeModificationManager for an unknown RegistryAccess: " + registryAccess);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profilerFiller) {
		this.reset();
		SelectionSpace biomeEntries = this.getUnmodifiedEntries();
		RegistryReadOps<JsonElement> readOps = this.readOps;
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;
			try {
				TargetedModifier<BiomeLoadingEvent, RegistryWriteOps<JsonElement>, RegistryReadOps<JsonElement>> targetedModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), readOps, BiomeModifiers.REGISTRY);
				this.addModifiers(targetedModifier.getTargetSelector().getTargetNames(biomeEntries), targetedModifier.getPriority(), targetedModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				Blueprint.LOGGER.error("Parsing error loading Biome Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		Blueprint.LOGGER.info("Biome Modification Manager has assigned {} sets of modifiers", this.size());
	}
}
