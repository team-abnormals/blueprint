package com.teamabnormals.blueprint.common.loot.modification.modifiers;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.modification.IModifier;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * An interface extending the {@link IModifier} interface, typed to be used on loot tables.
 *
 * @param <C> The type of config object for this modifier.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 */
public interface ILootModifier<C> extends IModifier<LootTableLoadEvent, C, Gson, Pair<Gson, PredicateManager>> {
}
