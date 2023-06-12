package com.teamabnormals.blueprint.common.loot.modification;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.LootModifier;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.ObjectModificationManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Data manager class for the {@link LootModifier} system.
 *
 * @author SmellyModder (Luke Tonon)
 */
//TODO: Remove this when Remolder is real!
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class LootModificationManager extends ObjectModificationManager<LootTable, Gson, Pair<Gson, LootDataManager>> {
	public static final String TARGET_DIRECTORY = "loot_tables";
	private static final Gson GSON = Deserializers.createLootTableSerializer().create();
	private static LootModificationManager INSTANCE = null;

	private LootModificationManager(LootDataManager lootDataManager) {
		super(GSON, TARGET_DIRECTORY, null, "Loot", LootModifierSerializers.REGISTRY, Pair.of(GSON, lootDataManager), true, true);
	}

	static {
		registerInitializer("LootDataManager", (registryAccess, commandSelection, reloadableServerResources) -> INSTANCE = new LootModificationManager(reloadableServerResources.getLootData()));
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		FileToIdConverter fileToIdConverter = FileToIdConverter.json(LootDataType.TABLE.directory());
		var locations = fileToIdConverter.listMatchingResources(resourceManager).keySet().stream().map(fileToIdConverter::fileToId);
		this.selectionSpace = locations::forEach;
		super.apply(map, resourceManager, profilerFiller);
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
}
