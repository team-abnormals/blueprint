package com.teamabnormals.blueprint.common.advancement.modification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.IAdvancementModifier;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.events.AdvancementBuildingEvent;
import com.teamabnormals.blueprint.core.util.modification.ModificationManager;
import com.teamabnormals.blueprint.core.util.modification.TargetedModifier;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Data manager class for {@link IAdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class AdvancementModificationManager extends ModificationManager<Builder, Void, DeserializationContext> {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static AdvancementModificationManager INSTANCE;
	private final PredicateManager lootPredicateManager;

	private AdvancementModificationManager(PredicateManager lootPredicateManager) {
		super(GSON, "modifiers/advancements", "advancements");
		this.lootPredicateManager = lootPredicateManager;
	}

	static {
		for (EventPriority priority : EventPriority.values()) {
			MinecraftForge.EVENT_BUS.addListener(priority, (AdvancementBuildingEvent event) -> {
				//Should not happen, but it's possible that this event will get fired before the manager is initialized
				if (INSTANCE != null) {
					var modifiers = INSTANCE.getModifiers(event.getLocation(), priority);
					if (modifiers != null) {
						Advancement.Builder builder = event.getBuilder();
						modifiers.forEach(configured -> configured.modify(builder));
					}
				}
			});
		}
	}

	/**
	 * Gets the instance of the {@link AdvancementModificationManager}.
	 * <p>This is initialized once it has been added as a reload listener.</p>
	 *
	 * @return The instance of the {@link AdvancementModificationManager}.
	 */
	public static AdvancementModificationManager getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) throws NoSuchFieldException, IllegalAccessException {
		INSTANCE = new AdvancementModificationManager(event.getServerResources().getPredicateManager());
		//Advancement modifiers must load before advancements
		Field field = AddReloadListenerEvent.class.getDeclaredField("serverResources");
		field.setAccessible(true);
		ReloadableResourceManager reloadableResourceManager = (ReloadableResourceManager) ((ReloadableServerResources) field.get(event)).getResourceManager();

		List<PreparableReloadListener> reloadListeners = ObfuscationReflectionHelper.getPrivateValue(ReloadableResourceManager.class, reloadableResourceManager, "f_203816_");
		if (reloadListeners != null) {
			reloadListeners.add(4, INSTANCE);
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.reset();
		SelectionSpace unmodifiedAdvancements = this.getUnmodifiedEntries();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;

			try {
				TargetedModifier<Builder, Void, DeserializationContext> targetedAdvancementModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), "advancement", new DeserializationContext(resourcelocation, this.lootPredicateManager), AdvancementModifiers.REGISTRY, true);
				this.addModifiers(targetedAdvancementModifier.getTargetSelector().getTargetNames(unmodifiedAdvancements), targetedAdvancementModifier.getPriority(), targetedAdvancementModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				Blueprint.LOGGER.error("Parsing error loading Advancement Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		Blueprint.LOGGER.info("Advancement Modification Manager has assigned {} sets of modifiers", this.size());
	}
}
