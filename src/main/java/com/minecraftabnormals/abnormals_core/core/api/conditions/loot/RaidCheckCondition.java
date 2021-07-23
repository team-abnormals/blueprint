package com.minecraftabnormals.abnormals_core.core.api.conditions.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.minecraftabnormals.abnormals_core.core.registry.ACLootConditions;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.registry.Registry;

/**
 * A loot condition that passes if there is a raid at the entity's position.
 *
 <p>Arguments:
 * <ul>
 *   <li>{@code inverted} (optional) - whether the condition should be inverted, so it will pass if there is not a raid instead.</li>
 * </ul></p>
 * @author abigailfails
 */
public class RaidCheckCondition implements ILootCondition {
    private final boolean inverted;

    public RaidCheckCondition(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public LootConditionType getType() {
        return Registry.LOOT_CONDITION_TYPE.get(ACLootConditions.RAID_CHECK);
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootParameters.THIS_ENTITY);
        return inverted != (entity != null && lootContext.getLevel().getRaidAt(entity.blockPosition()) != null);
    }

    public static class Serializer implements ILootSerializer<RaidCheckCondition> {

        @Override
        public void serialize(JsonObject json, RaidCheckCondition condition, JsonSerializationContext context) {
            if (condition.inverted)
                json.addProperty("inverted", true);
        }

        @Override
        public RaidCheckCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new RaidCheckCondition(JSONUtils.getAsBoolean(json, "inverted", false));
        }
    }
}
