package com.minecraftabnormals.abnormals_core.core.api.conditions.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.registry.ACLootConditions;
import net.minecraft.core.Registry;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
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
		return Registry.LOOT_CONDITION_TYPE.get(ACLootConditions.RANDOM_DIFFICULTY_CHANCE);
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

	public static class RandomDifficultyChanceSerializer implements Serializer<RandomDifficultyChanceCondition> {

		private static float getFloatOrMinus1(JsonObject json, String fieldName) {
			return json.has(fieldName) ? GsonHelper.getAsFloat(json, fieldName) : -1.0F;
		}

		public void serialize(JsonObject json, RandomDifficultyChanceCondition condition, JsonSerializationContext context) {
			json.addProperty("default_chance", condition.defaultChance);
			if (condition.peacefulChance >= 0)
				json.addProperty("peaceful", condition.peacefulChance);
			if (condition.easyChance >= 0)
				json.addProperty("easy", condition.easyChance);
			if (condition.normalChance >= 0)
				json.addProperty("normal", condition.normalChance);
			if (condition.hardChance >= 0)
				json.addProperty("hard", condition.hardChance);
		}

		public RandomDifficultyChanceCondition deserialize(JsonObject json, JsonDeserializationContext context) {
			if (json.has("default_chance")) {
				return new RandomDifficultyChanceCondition(GsonHelper.getAsFloat(json, "default_chance"), getFloatOrMinus1(json, "peaceful"), getFloatOrMinus1(json, "easy"), getFloatOrMinus1(json, "normal"), getFloatOrMinus1(json, "hard"));
			}
			throw new JsonSyntaxException("Missing 'default_chance', expected to find a float");
		}

	}
}
