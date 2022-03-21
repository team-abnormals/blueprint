package com.teamabnormals.blueprint.common.loot.modification.modifiers;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifier;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * An interface extending the {@link ObjectModifier} interface, typed to be used on loot tables.
 *
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifier
 */
public interface LootModifier<M extends LootModifier<M>> extends ObjectModifier<LootTableLoadEvent, Gson, Pair<Gson, PredicateManager>, M> {
	/**
	 * A {@link ObjectModifier.Serializer} extension, typed to be used for {@link LootModifier} types.
	 *
	 * @param <M> The type of {@link LootModifier} instances to serialize and deserialize.
	 * @author SmellyModder (Luke Tonon)
	 */
	interface Serializer<M extends LootModifier<M>> extends ObjectModifier.Serializer<M, Gson, Pair<Gson, PredicateManager>> {}
}
