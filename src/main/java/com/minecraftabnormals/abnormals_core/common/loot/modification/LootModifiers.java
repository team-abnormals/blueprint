package com.minecraftabnormals.abnormals_core.common.loot.modification;

import com.google.gson.Gson;
import com.minecraftabnormals.abnormals_core.common.loot.modification.modifiers.*;
import com.minecraftabnormals.abnormals_core.core.util.modification.ModifierRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.loot.LootPredicateManager;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * A registry class for {@link com.minecraftabnormals.abnormals_core.common.loot.modification.modifiers.ILootModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootModifiers {
	public static final ModifierRegistry<LootTableLoadEvent, Gson, Pair<Gson, LootPredicateManager>> REGISTRY = new ModifierRegistry<>();

	public static final LootPoolEntriesModifier ENTRIES_MODIFIER = REGISTRY.register("entries", new LootPoolEntriesModifier());
	public static final LootPoolsModifier POOLS_MODIFIER = REGISTRY.register("pools", new LootPoolsModifier());
	public static final LootTypeModifier TYPE_MODIFIER = REGISTRY.register("type", new LootTypeModifier());
}
