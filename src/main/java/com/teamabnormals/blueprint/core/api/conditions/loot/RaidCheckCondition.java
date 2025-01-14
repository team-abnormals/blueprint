package com.teamabnormals.blueprint.core.api.conditions.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.core.registry.BlueprintLootConditions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
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
	public static final MapCodec<RaidCheckCondition> CODEC = Codec.BOOL.optionalFieldOf("inverted", false).xmap(RaidCheckCondition::new, condition -> condition.inverted);
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
}
