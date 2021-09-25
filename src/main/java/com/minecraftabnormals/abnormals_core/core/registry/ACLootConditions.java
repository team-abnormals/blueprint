package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.api.conditions.loot.RaidCheckCondition.RaidCheckSerializer;
import com.minecraftabnormals.abnormals_core.core.api.conditions.loot.RandomDifficultyChanceCondition.RandomDifficultyChanceSerializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

/**
 * Registry class for Abnormals Core's built-in loot conditions.
 *
 * <p>These conditions can be used by mods and datapacks.</p>
 *
 * @author abigailfails
 */
public final class ACLootConditions {
    public static final ResourceLocation RANDOM_DIFFICULTY_CHANCE = new ResourceLocation(AbnormalsCore.MODID, "random_difficulty_chance");
    public static final ResourceLocation RAID_CHECK = new ResourceLocation(AbnormalsCore.MODID, "raid_check");

    public static void registerLootConditions() {
        Registry.register(Registry.LOOT_CONDITION_TYPE, RANDOM_DIFFICULTY_CHANCE, new LootItemConditionType(new RandomDifficultyChanceSerializer()));
        Registry.register(Registry.LOOT_CONDITION_TYPE, RAID_CHECK, new LootItemConditionType(new RaidCheckSerializer()));
    }
}
