package com.teamabnormals.blueprint.common.advancement.modification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.AdvancementModifier;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.events.AdvancementBuildingEvent;
import com.teamabnormals.blueprint.core.util.modification.ObjectModificationManager;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/**
 * Data manager class for the {@link AdvancementModifier} system.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class AdvancementModificationManager extends ObjectModificationManager<Builder, Void, DeserializationContext> {
	public static final String TARGET_PATH = "advancements";
	private static final Gson GSON = (new GsonBuilder()).create();
	private static AdvancementModificationManager INSTANCE;

	private AdvancementModificationManager(LootDataManager lootData) {
		super(GSON, TARGET_PATH, TARGET_PATH, "Advancement", AdvancementModifierSerializers.REGISTRY, (location) -> new DeserializationContext(location, lootData), true, true);
	}

	static {
		registerInitializer("ServerAdvancementManager", (registryAccess, commandSelection, reloadableServerResources) -> INSTANCE = new AdvancementModificationManager(reloadableServerResources.getLootData()));
		for (EventPriority priority : EventPriority.values()) {
			MinecraftForge.EVENT_BUS.addListener(priority, (AdvancementBuildingEvent event) -> {
				//Should not happen, but it's possible that this event will get fired before the manager is initialized
				if (INSTANCE != null) INSTANCE.applyModifiers(priority, event.getLocation(), event.getBuilder());
			});
		}
	}

	/**
	 * Gets the instance of the {@link AdvancementModificationManager}.
	 * <p>This is initialized once it has been added as a reload listener.</p>
	 *
	 * @return The instance of the {@link AdvancementModificationManager}.
	 */
	@Nullable
	public static AdvancementModificationManager getInstance() {
		return INSTANCE;
	}
}
