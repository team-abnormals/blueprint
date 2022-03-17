package com.teamabnormals.blueprint.common.loot.modification;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.*;
import com.teamabnormals.blueprint.core.util.modification.ModifierRegistry;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * A registry class for {@link ILootModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootModifiers {
	public static final ModifierRegistry<LootTableLoadEvent, Gson, Pair<Gson, PredicateManager>> REGISTRY = new ModifierRegistry<>();

	public static final LootPoolEntriesModifier ENTRIES_MODIFIER = REGISTRY.register("entries", new LootPoolEntriesModifier());
	public static final LootPoolsModifier POOLS_MODIFIER = REGISTRY.register("pools", new LootPoolsModifier());
	public static final LootTypeModifier TYPE_MODIFIER = REGISTRY.register("type", new LootTypeModifier());
}
