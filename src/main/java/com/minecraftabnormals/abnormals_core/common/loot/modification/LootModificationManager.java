package com.minecraftabnormals.abnormals_core.common.loot.modification;

import com.google.gson.*;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.util.modification.ConfiguredModifier;
import com.minecraftabnormals.abnormals_core.core.util.modification.ModificationManager;
import com.minecraftabnormals.abnormals_core.core.util.modification.TargetedModifier;
import com.mojang.datafixers.util.Pair;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Data manager class for {@link com.minecraftabnormals.abnormals_core.common.loot.modification.modifiers.ILootModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public final class LootModificationManager extends ModificationManager<LootTableLoadEvent, Gson, Pair<Gson, LootPredicateManager>> {
	private static final Gson GSON = LootSerializers.createLootTableSerializer().registerTypeAdapter(LootPool.class, new LootPoolSerializer()).create();
	private static LootModificationManager INSTANCE = null;
	private final LootPredicateManager lootPredicateManager;

	private LootModificationManager(LootPredicateManager lootPredicateManager) {
		super(GSON, "modifiers/loot_tables");
		this.lootPredicateManager = lootPredicateManager;
	}

	/**
	 * Gets the instance of the {@link LootModificationManager}.
	 * <p>This is initialized once it has been added as a reload listener.</p>
	 *
	 * @return The instance of the {@link LootModificationManager}.
	 */
	public static LootModificationManager getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		List<ConfiguredModifier<LootTableLoadEvent, ?, Gson, Pair<Gson, LootPredicateManager>, ?>> configuredModifiers = INSTANCE.getModifiers(event.getName());
		if (configuredModifiers != null) {
			configuredModifiers.forEach(configuredModifier -> configuredModifier.modify(event));
		}
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) {
		DataPackRegistries dataPackRegistries = event.getDataPackRegistries();
		INSTANCE = new LootModificationManager(dataPackRegistries.getPredicateManager());
		//Loot modifiers must load before loot tables
		SimpleReloadableResourceManager simpleReloadableResourceManager = (SimpleReloadableResourceManager) dataPackRegistries.getResourceManager();
		List<IFutureReloadListener> reloadListeners = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, simpleReloadableResourceManager, "field_199015_d");
		if (reloadListeners != null) {
			reloadListeners.add(2, INSTANCE);
		}

		List<IFutureReloadListener> initTaskQueue = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, simpleReloadableResourceManager, "field_219539_d");
		if (initTaskQueue != null) {
			initTaskQueue.add(2, INSTANCE);
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManager, IProfiler profiler) {
		this.reset();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;
			try {
				TargetedModifier<LootTableLoadEvent, Gson, Pair<Gson, LootPredicateManager>> targetedModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), Pair.of(GSON, this.lootPredicateManager), LootModifiers.REGISTRY);
				this.addModifiers(targetedModifier.getTarget(), targetedModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				AbnormalsCore.LOGGER.error("Parsing error loading Loot Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		AbnormalsCore.LOGGER.info("Loot Modification Manager has loaded {} sets of modifiers", this.size());
	}

	//Thanks forge...
	static class LootPoolSerializer extends LootPool.Serializer {
		private static Constructor<LootPool> LOOT_POOL_CONSTRUCTOR;

		static {
			try {
				LOOT_POOL_CONSTRUCTOR = LootPool.class.getDeclaredConstructor(LootEntry[].class, ILootCondition[].class, ILootFunction[].class, IRandomRange.class, RandomValueRange.class, String.class);
				LOOT_POOL_CONSTRUCTOR.setAccessible(true);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		@Override
		public LootPool deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
			JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "loot pool");
			LootEntry[] alootentry = JSONUtils.getAsObject(jsonobject, "entries", p_deserialize_3_, LootEntry[].class);
			ILootCondition[] ailootcondition = JSONUtils.getAsObject(jsonobject, "conditions", new ILootCondition[0], p_deserialize_3_, ILootCondition[].class);
			ILootFunction[] ailootfunction = JSONUtils.getAsObject(jsonobject, "functions", new ILootFunction[0], p_deserialize_3_, ILootFunction[].class);
			IRandomRange irandomrange = RandomRanges.deserialize(jsonobject.get("rolls"), p_deserialize_3_);
			RandomValueRange randomvaluerange = JSONUtils.getAsObject(jsonobject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), p_deserialize_3_, RandomValueRange.class);
			if (jsonobject.has("name")) {
				try {
					return LOOT_POOL_CONSTRUCTOR.newInstance(alootentry, ailootcondition, ailootfunction, irandomrange, randomvaluerange, JSONUtils.getAsString(jsonobject, "name"));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
					throw new JsonParseException("Could not initialize a new loot pool: " + e);
				}
			} else {
				throw new JsonParseException("Missing name for loot pool!");
			}
		}
	}
}
