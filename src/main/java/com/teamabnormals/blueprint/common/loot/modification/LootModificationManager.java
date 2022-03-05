package com.teamabnormals.blueprint.common.loot.modification;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.ILootModifier;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.ModificationManager;
import com.teamabnormals.blueprint.core.util.modification.TargetedModifier;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Data manager class for {@link ILootModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class LootModificationManager extends ModificationManager<LootTableLoadEvent, Gson, Pair<Gson, PredicateManager>> {
	private static final Gson GSON = Deserializers.createLootTableSerializer().registerTypeAdapter(LootPool.class, new LootPoolSerializer()).create();
	private static LootModificationManager INSTANCE = null;
	private final PredicateManager lootPredicateManager;

	private LootModificationManager(PredicateManager lootPredicateManager) {
		super(GSON, "modifiers/loot_tables", "loot_tables");
		this.lootPredicateManager = lootPredicateManager;
	}

	static {
		for (EventPriority priority : EventPriority.values()) {
			MinecraftForge.EVENT_BUS.addListener(priority, (LootTableLoadEvent event) -> {
				//Should not happen, but it's possible that this event will get fired before the manager is initialized
				if (INSTANCE != null) {
					var modifiers = INSTANCE.getModifiers(event.getName(), priority);
					if (modifiers != null) {
						modifiers.forEach(configured -> configured.modify(event));
					}
				}
			});
		}
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
	public static void onReloadListener(AddReloadListenerEvent event) {
		ReloadableServerResources dataPackRegistries = ServerLifecycleHooks.getCurrentServer().getServerResources().managers();
		INSTANCE = new LootModificationManager(dataPackRegistries.getPredicateManager());
		//Loot modifiers must load before loot tables
		ReloadableResourceManager simpleReloadableResourceManager = (ReloadableResourceManager) dataPackRegistries.getResourceManager();
		List<PreparableReloadListener> reloadListeners = ObfuscationReflectionHelper.getPrivateValue(ReloadableResourceManager.class, simpleReloadableResourceManager, "f_10871_");
		if (reloadListeners != null) {
			reloadListeners.add(2, INSTANCE);
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.reset();
		SelectionSpace unmodifiedTables = this.getUnmodifiedEntries();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;
			try {
				TargetedModifier<LootTableLoadEvent, Gson, Pair<Gson, PredicateManager>> targetedModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), Pair.of(GSON, this.lootPredicateManager), LootModifiers.REGISTRY);
				this.addModifiers(targetedModifier.getTargetSelector().getTargetNames(unmodifiedTables), targetedModifier.getPriority(), targetedModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				Blueprint.LOGGER.error("Parsing error loading Loot Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		Blueprint.LOGGER.info("Loot Modification Manager has assigned {} sets of modifiers", this.size());
	}

	//Thanks forge...
	static class LootPoolSerializer extends LootPool.Serializer {
		private static Constructor<LootPool> LOOT_POOL_CONSTRUCTOR;

		static {
			try {
				LOOT_POOL_CONSTRUCTOR = LootPool.class.getDeclaredConstructor(LootPoolEntryContainer[].class, LootItemCondition[].class, LootItemFunction[].class, NumberProvider.class, NumberProvider.class, String.class);
				LOOT_POOL_CONSTRUCTOR.setAccessible(true);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		@Override
		public LootPool deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonobject = GsonHelper.convertToJsonObject(element, "loot pool");
			LootPoolEntryContainer[] alootentry = GsonHelper.getAsObject(jsonobject, "entries", context, LootPoolEntryContainer[].class);
			LootItemCondition[] ailootcondition = GsonHelper.getAsObject(jsonobject, "conditions", new LootItemCondition[0], context, LootItemCondition[].class);
			LootItemFunction[] ailootfunction = GsonHelper.getAsObject(jsonobject, "functions", new LootItemFunction[0], context, LootItemFunction[].class);
			NumberProvider rolls = GsonHelper.getAsObject(jsonobject, "rolls", context, NumberProvider.class);
			NumberProvider bonusRolls = GsonHelper.getAsObject(jsonobject, "bonus_rolls", ConstantValue.exactly(0.0F), context, NumberProvider.class);
			if (jsonobject.has("name")) {
				try {
					return LOOT_POOL_CONSTRUCTOR.newInstance(alootentry, ailootcondition, ailootfunction, rolls, bonusRolls, GsonHelper.getAsString(jsonobject, "name"));
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
