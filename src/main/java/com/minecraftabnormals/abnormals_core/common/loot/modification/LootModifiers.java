package com.minecraftabnormals.abnormals_core.common.loot.modification;

import com.google.gson.Gson;
import com.minecraftabnormals.abnormals_core.common.loot.modification.modifiers.*;
import com.minecraftabnormals.abnormals_core.core.util.modification.ModifierDataProvider;
import com.minecraftabnormals.abnormals_core.core.util.modification.ModifierRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootSerializers;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * A registry class for {@link ILootModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootModifiers {
	public static final ModifierRegistry<LootTableLoadEvent, Gson, Pair<Gson, LootPredicateManager>> REGISTRY = new ModifierRegistry<>();
	private static final Gson GSON = LootSerializers.createLootTableSerializer().setPrettyPrinting().disableHtmlEscaping().create();

	public static final LootPoolEntriesModifier ENTRIES_MODIFIER = REGISTRY.register("entries", new LootPoolEntriesModifier());
	public static final LootPoolsModifier POOLS_MODIFIER = REGISTRY.register("pools", new LootPoolsModifier());
	public static final LootTypeModifier TYPE_MODIFIER = REGISTRY.register("type", new LootTypeModifier());

	/**
	 * Creates a new {@link ModifierDataProvider} for {@link ILootModifier}s.
	 *
	 * @param dataGenerator A {@link DataGenerator} to use when generating the configured {@link ILootModifier}s.
	 * @param name          A name for the provider.
	 * @param modId         The ID of the mod using this provider.
	 * @param toGenerate    An array of {@link ModifierDataProvider.ProviderEntry}s to generate.
	 * @return A new {@link ModifierDataProvider} for {@link ILootModifier}s.
	 */
	@SafeVarargs
	public static ModifierDataProvider<LootTableLoadEvent, Gson, Pair<Gson, LootPredicateManager>> createDataProvider(DataGenerator dataGenerator, String name, String modId, ModifierDataProvider.ProviderEntry<LootTableLoadEvent, Gson, Pair<Gson, LootPredicateManager>>... toGenerate) {
		return new ModifierDataProvider<>(dataGenerator, name, GSON, modId, "modifiers/loot_tables", REGISTRY, GSON, toGenerate);
	}
}
