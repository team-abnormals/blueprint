package com.minecraftabnormals.abnormals_core.common.advancement.modification;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.events.AdvancementBuildingEvent;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The class that handles the deserialization of {@link ConfiguredAdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public final class AdvancementModificationManager extends JsonReloadListener {
	private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(TargetedAdvancementModifier.class, new TargetedAdvancementModifierDeserializer()).create();
	private static final Map<ResourceLocation, List<ConfiguredAdvancementModifier<?, ?>>> MODIFIERS = Maps.newHashMap();

	public AdvancementModificationManager() {
		super(GSON, "modifiers/advancements");
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) throws NoSuchFieldException, IllegalAccessException {
		//Advancement modifiers must load before advancements
		Field field = AddReloadListenerEvent.class.getDeclaredField("dataPackRegistries");
		field.setAccessible(true);
		SimpleReloadableResourceManager reloadableResourceManager = (SimpleReloadableResourceManager) ((DataPackRegistries) field.get(event)).getResourceManager();

		List<IFutureReloadListener> reloadListeners = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, reloadableResourceManager, "reloadListeners");
		if (reloadListeners != null) {
			reloadListeners.add(4, AbnormalsCore.ADVANCEMENT_MODIFICATION_MANAGER);
		}

		List<IFutureReloadListener> initTaskQueue = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, reloadableResourceManager, "initTaskQueue");
		if (initTaskQueue != null) {
			initTaskQueue.add(4, AbnormalsCore.ADVANCEMENT_MODIFICATION_MANAGER);
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

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManager, IProfiler profiler) {
		MODIFIERS.clear();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;

			try {
				TargetedAdvancementModifier targetedAdvancementModifier = GSON.fromJson(entry.getValue(), TargetedAdvancementModifier.class);
				MODIFIERS.computeIfAbsent(targetedAdvancementModifier.getTarget(), target -> {
					return new ArrayList<>();
				}).addAll(targetedAdvancementModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				AbnormalsCore.LOGGER.error("Parsing error loading Advancement Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		AbnormalsCore.LOGGER.info("Advancement Modification Manager has loaded {} sets of modifiers", MODIFIERS.size());
	}

	public static class TargetedAdvancementModifierDeserializer implements JsonDeserializer<TargetedAdvancementModifier> {

		@Override
		public TargetedAdvancementModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			DataResult<Pair<TargetedAdvancementModifier, JsonElement>> decode = TargetedAdvancementModifier.CODEC.decode(JsonOps.INSTANCE, json.getAsJsonObject());

			Optional<Pair<TargetedAdvancementModifier, JsonElement>> result = decode.result();
			if (result.isPresent()) {
				return result.get().getFirst();
			}

			Optional<DataResult.PartialResult<Pair<TargetedAdvancementModifier, JsonElement>>> error = decode.error();
			if (error.isPresent()) {
				throw new JsonParseException(error.get().toString());
			}
			throw new JsonParseException("No result was found from the decode of the TargetedAdvancementModifier");
		}

	}
}
