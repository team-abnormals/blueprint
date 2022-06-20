package com.teamabnormals.blueprint.core.api.conditions.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.teamabnormals.blueprint.core.registry.BlueprintLootConditions;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * A {@link LootItemCondition} implementation that passes if there is a raid at the entity's position.
 *
 * <p>Arguments:
 * <ul>
 *   <li>{@code inverted} (optional) - whether the condition should be inverted, so it will pass if there is not a raid instead.</li>
 * </ul></p>
 *
 * @author abigailfails
 */
public class RaidCheckCondition implements LootItemCondition {
	private final boolean inverted;

	public RaidCheckCondition(boolean inverted) {
		this.inverted = inverted;
	}

	@Override
	public LootItemConditionType getType() {
		return BlueprintLootConditions.RAID_CHECK.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
		return inverted != (entity != null && lootContext.getLevel().getRaidAt(entity.blockPosition()) != null);
	}

	public static class RaidCheckSerializer implements Serializer<RaidCheckCondition> {

		@Override
		public void serialize(JsonObject json, RaidCheckCondition condition, JsonSerializationContext context) {
			if (condition.inverted)
				json.addProperty("inverted", true);
		}

		@Override
		public RaidCheckCondition deserialize(JsonObject json, JsonDeserializationContext context) {
			return new RaidCheckCondition(GsonHelper.getAsBoolean(json, "inverted", false));
		}
	}
}
