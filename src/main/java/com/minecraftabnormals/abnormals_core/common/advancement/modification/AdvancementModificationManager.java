package com.minecraftabnormals.abnormals_core.common.advancement.modification;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers.AdvancementModifiers;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.events.AdvancementBuildingEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class that handles the deserialization of {@link ConfiguredAdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public final class AdvancementModificationManager extends JsonReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Map<ResourceLocation, List<ConfiguredAdvancementModifier<?, ?>>> MODIFIERS = Maps.newHashMap();
	private static AdvancementModificationManager INSTANCE;

	private final LootPredicateManager lootPredicateManager;

	private AdvancementModificationManager(LootPredicateManager lootPredicateManager) {
		super(GSON, "modifiers/advancements");
		this.lootPredicateManager = lootPredicateManager;
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
		INSTANCE = new AdvancementModificationManager(event.getDataPackRegistries().getPredicateManager());
		//Advancement modifiers must load before advancements
		Field field = AddReloadListenerEvent.class.getDeclaredField("dataPackRegistries");
		field.setAccessible(true);
		SimpleReloadableResourceManager reloadableResourceManager = (SimpleReloadableResourceManager) ((DataPackRegistries) field.get(event)).getResourceManager();

		List<IFutureReloadListener> reloadListeners = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, reloadableResourceManager, "field_199015_d");
		if (reloadListeners != null) {
			reloadListeners.add(4, INSTANCE);
		}

		List<IFutureReloadListener> initTaskQueue = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, reloadableResourceManager, "field_219539_d");
		if (initTaskQueue != null) {
			initTaskQueue.add(4, INSTANCE);
		}
	}

	@SubscribeEvent
	public static void onBuildingAdvancement(AdvancementBuildingEvent event) {
		List<ConfiguredAdvancementModifier<?, ?>> modifiers = MODIFIERS.get(event.getLocation());
		if (modifiers != null) {
			Advancement.Builder builder = event.getBuilder();
			modifiers.forEach(modifier -> {
				modifier.modify(builder);
			});
		}
	}

	private static TargetedAdvancementModifier deserializeModifiers(JsonElement jsonElement, ConditionArrayParser conditionArrayParser) throws JsonParseException {
		JsonObject object = jsonElement.getAsJsonObject();
		ResourceLocation advancement = new ResourceLocation(JSONUtils.getAsString(object, "advancement"));
		List<ConfiguredAdvancementModifier<?, ?>> advancementModifiers = Lists.newArrayList();
		JsonArray modifiers = JSONUtils.getAsJsonArray(object, "modifiers");
		modifiers.forEach(element -> {
			JsonObject entry = element.getAsJsonObject();
			String type = JSONUtils.getAsString(entry, "type");
			if (!JSONUtils.isValidNode(entry, "conditions") || CraftingHelper.processConditions(JSONUtils.getAsJsonArray(entry, "conditions"))) {
				AdvancementModifier<?> modifier = AdvancementModifiers.getModifier(type);
				if (modifier == null) {
					throw new JsonParseException("Unknown Advancement Modifier type: " + type);
				}
				JsonElement config = entry.get("config");
				if (config == null) {
					throw new JsonParseException("Missing 'config' element!");
				}
				advancementModifiers.add(modifier.deserialize(config, conditionArrayParser));
			} else AbnormalsCore.LOGGER.info("Skipped advancement modifier \"" + type + "\" for advancement \"" + advancement + "\" as its conditions were not met");
		});
		return new TargetedAdvancementModifier(advancement, advancementModifiers);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManager, IProfiler profiler) {
		MODIFIERS.clear();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;

			try {
				TargetedAdvancementModifier targetedAdvancementModifier = deserializeModifiers(entry.getValue(), new ConditionArrayParser(resourcelocation, this.lootPredicateManager));
				MODIFIERS.computeIfAbsent(targetedAdvancementModifier.getTarget(), target -> {
					return new ArrayList<>();
				}).addAll(targetedAdvancementModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				AbnormalsCore.LOGGER.error("Parsing error loading Advancement Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		AbnormalsCore.LOGGER.info("Advancement Modification Manager has loaded {} sets of modifiers", MODIFIERS.size());
	}
}
