package com.teamabnormals.blueprint.core.api.conditions.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.registry.BlueprintLootConditions;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * A {@link LootItemCondition} implementation that defines a probability based on the current difficulty. Works the same as
 * {@code random_difficulty_chance} in Bedrock edition.
 *
 * <p>Arguments:
 * <ul>
 *   <li>{@code default_chance} (required) - the float chance to fall back on if the difficulty is set a value
 *                                           other than those specified in the arguments.</li>
 *   <li>{@code hard} - the float chance to use if the difficulty is set to Hard.</li>
 *   <li>{@code normal} - the float chance to use if the difficulty is set to Normal.</li>
 *   <li>{@code easy} - the float chance to use if the difficulty is set to Easy.</li>
 *   <li>{@code peaceful} - the float chance to use if the difficulty is set to Peaceful.</li>
 * </ul></p>
 *
 * @author abigailfails
 */
public class RandomDifficultyChanceCondition implements LootItemCondition {
	public static final MapCodec<RandomDifficultyChanceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("default_chance").forGetter(condition -> condition.defaultChance),
		Codec.FLOAT.optionalFieldOf("peaceful", -1.0F).forGetter(condition -> condition.peacefulChance),
		Codec.FLOAT.optionalFieldOf("easy", -1.0F).forGetter(condition -> condition.peacefulChance),
		Codec.FLOAT.optionalFieldOf("normal", -1.0F).forGetter(condition -> condition.peacefulChance),
		Codec.FLOAT.optionalFieldOf("hard", -1.0F).forGetter(condition -> condition.peacefulChance)
	).apply(instance, RandomDifficultyChanceCondition::new));
	private final float defaultChance;
	private final float peacefulChance;
	private final float easyChance;
	private final float normalChance;
	private final float hardChance;

	public RandomDifficultyChanceCondition(float defaultChance, float peacefulChance, float easyChance, float normalChance, float hardChance) {
		this.defaultChance = defaultChance;
		this.peacefulChance = peacefulChance;
		this.easyChance = easyChance;
		this.normalChance = normalChance;
		this.hardChance = hardChance;
	}

	@Override
	public LootItemConditionType getType() {
		return BlueprintLootConditions.RANDOM_DIFFICULTY_CHANCE.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		float chance = this.defaultChance;
		switch (lootContext.getLevel().getDifficulty()) {
			case PEACEFUL:
				if (this.peacefulChance >= 0) chance = this.peacefulChance;
				break;
			case EASY:
				if (this.easyChance >= 0) chance = this.easyChance;
				break;
			case NORMAL:
				if (this.normalChance >= 0) chance = this.normalChance;
				break;
			case HARD:
				if (this.hardChance >= 0) chance = this.hardChance;
		}
		return lootContext.getRandom().nextFloat() < chance;
	}
}
