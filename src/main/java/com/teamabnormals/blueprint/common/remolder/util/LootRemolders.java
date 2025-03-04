package com.teamabnormals.blueprint.common.remolder.util;

import com.teamabnormals.blueprint.common.remolder.Remolder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;

import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.*;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.target;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.value;

/**
 * Utility class for creating {@link Remolder} instances for modifying loot tables.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class LootRemolders {
	/**
	 * Creates a {@link Remolder} instance that replaces the context parameter set of loot tables.
	 *
	 * @param set The {@link LootContextParamSet} instance to replace with.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder replaceType(LootContextParamSet set) {
		return replace(target("type"), value(set, LootContextParamSets.CODEC));
	}

	/**
	 * Creates a {@link Remolder} instance that replaces the pools of loot tables.
	 *
	 * @param pools The {@link LootPool} instances to replace with.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder replacePools(LootPool... pools) {
		return replace(target("pools"), value(List.of(pools), LootPool.CODEC.listOf()));
	}

	/**
	 * Creates a {@link Remolder} instance that adds a pool to loot tables.
	 *
	 * @param pool The {@link LootPool} instances to add.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder addPool(LootPool pool) {
		return add(target("pools[]"), value(pool, LootPool.CODEC));
	}

	/**
	 * Creates a {@link Remolder} instance that replaces entries of a specific pool in loot tables.
	 *
	 * @param entries The {@link LootPoolEntryContainer} instances to replace with.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder replaceEntries(int index, LootPoolEntryContainer... entries) {
		return replace(target("pools[" + index + "].entries"), value(List.of(entries), LootPoolEntries.CODEC.listOf()));
	}

	/**
	 * Creates a {@link Remolder} instance that adds a pool entry to a specific pool in loot tables.
	 *
	 * @param entry The {@link LootPoolEntryContainer} instance to add.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder addEntry(int index, LootPoolEntryContainer entry) {
		return add(target("pools[" + index + "].entries[]"), value(entry, LootPoolEntries.CODEC));
	}
}
