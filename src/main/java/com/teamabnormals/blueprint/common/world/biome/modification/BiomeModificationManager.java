package com.teamabnormals.blueprint.common.world.biome.modification;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.ModificationManager;
import com.teamabnormals.blueprint.core.util.modification.TargetedModifier;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Data manager class for the {@link com.teamabnormals.blueprint.common.world.biome.modification.modifiers.IBiomeModifier} system.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class BiomeModificationManager extends ModificationManager<BiomeLoadingEvent, Void, Void> {
	private static final Gson GSON = new Gson();
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

	private BiomeModificationManager() {
		super(GSON, "modifiers/worldgen/biome");
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

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) {
		event.addListener(INSTANCE = new BiomeModificationManager());
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profilerFiller) {
		this.reset();
		SelectionSpace biomeEntries = this.getUnmodifiedEntries();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;
			try {
				TargetedModifier<BiomeLoadingEvent, Void, Void> targetedModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), null, BiomeModifiers.REGISTRY);
				this.addModifiers(targetedModifier.getTargetSelector().getTargetNames(biomeEntries), targetedModifier.getPriority(), targetedModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				Blueprint.LOGGER.error("Parsing error loading Biome Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		Blueprint.LOGGER.info("Biome Modification Manager has assigned {} sets of modifiers", this.size());
	}
}
