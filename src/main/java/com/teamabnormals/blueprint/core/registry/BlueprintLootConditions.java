package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.api.conditions.loot.RaidCheckCondition;
import com.teamabnormals.blueprint.core.api.conditions.loot.RandomDifficultyChanceCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry class for Blueprint's built-in loot conditions.
 *
 * <p>These conditions can be used by mods and datapacks.</p>
 *
 * @author abigailfails
 * @author SmellyModder
 */
public final class BlueprintLootConditions {
	public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Blueprint.MOD_ID);

	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> RANDOM_DIFFICULTY_CHANCE = LOOT_CONDITION_TYPES.register("random_difficulty_chance", () -> new LootItemConditionType(RandomDifficultyChanceCondition.CODEC));
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> RAID_CHECK = LOOT_CONDITION_TYPES.register("raid_check", () -> new LootItemConditionType(RaidCheckCondition.CODEC));
}
