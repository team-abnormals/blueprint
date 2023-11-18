package com.teamabnormals.blueprint.common.loot.modification;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.LootModifier;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.ObjectModificationManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Data manager class for the {@link LootModifier} system.
 *
 * @author SmellyModder (Luke Tonon)
 */
//TODO: Remove this when Remolder is real!
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class LootModificationManager extends ObjectModificationManager<LootTable, Gson, Pair<Gson, LootDataManager>> {
	public static final String TARGET_DIRECTORY = "loot_tables";
	private static final Gson GSON = Deserializers.createLootTableSerializer().registerTypeAdapter(LootPool.class, new LootPoolSerializer()).create();
	private static LootModificationManager INSTANCE = null;

	private LootModificationManager(LootDataManager lootDataManager) {
		super(GSON, TARGET_DIRECTORY, null, LootModifierSerializers.REGISTRY, Pair.of(GSON, lootDataManager), true, true);
	}

	static {
		registerInitializer("LootDataManager", (registryAccess, commandSelection, reloadableServerResources) -> INSTANCE = new LootModificationManager(reloadableServerResources.getLootData()));
	}

	/**
	 * Gets the instance of the {@link LootModificationManager}.
	 * <p>This is initialized once it has been added as a reload listener.</p>
	 *
	 * @return The instance of the {@link LootModificationManager}.
	 */
	@Nullable
	public static LootModificationManager getInstance() {
		return INSTANCE;
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
