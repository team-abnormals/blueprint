package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.api.conditions.loot.RaidCheckCondition;
import com.minecraftabnormals.abnormals_core.core.api.conditions.loot.RandomDifficultyChanceCondition;
import net.minecraft.loot.LootConditionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * @author abigailfails
 */
public final class ACLootConditions {
    public static final ResourceLocation RANDOM_DIFFICULTY_CHANCE = new ResourceLocation(AbnormalsCore.MODID, "random_difficulty_chance");
    public static final ResourceLocation RAID_CHECK = new ResourceLocation(AbnormalsCore.MODID, "raid_check");

    public static void registerLootConditions() {
        Registry.register(Registry.LOOT_CONDITION_TYPE, RANDOM_DIFFICULTY_CHANCE, new LootConditionType(new RandomDifficultyChanceCondition.Serializer()));
        Registry.register(Registry.LOOT_CONDITION_TYPE, RAID_CHECK, new LootConditionType(new RaidCheckCondition.Serializer()));
    }
}
