package com.teamabnormals.blueprint.common.loot.modification;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.*;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifierSerializerRegistry;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * The registry class for {@link LootModifier} serializers.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LootModifierSerializers {
	public static final ObjectModifierSerializerRegistry<LootTableLoadEvent, Gson, Pair<Gson, LootDataManager>> REGISTRY = new ObjectModifierSerializerRegistry<>();

	public static final LootPoolEntriesModifier.Serializer ENTRIES = REGISTRY.register("entries", new LootPoolEntriesModifier.Serializer());
	public static final LootPoolsModifier.Serializer POOLS = REGISTRY.register("pools", new LootPoolsModifier.Serializer());
	public static final LootTypeModifier.Serializer TYPE = REGISTRY.register("type", new LootTypeModifier.Serializer());
}
