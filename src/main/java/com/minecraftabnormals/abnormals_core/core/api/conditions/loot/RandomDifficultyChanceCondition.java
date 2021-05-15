package com.minecraftabnormals.abnormals_core.core.api.conditions.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.registry.ACLootConditions;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.registry.Registry;

/**
 * A loot condition that defines a probability based on the current difficulty. Works the same as
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
public class RandomDifficultyChanceCondition implements ILootCondition {
	private final float defaultChance;
	private final float peacefulChance;
	private final float easyChance;
	private final float normalChance;
	private final float hardChance;

	public RandomDifficultyChanceCondition(float defaultChance, float peacefulChance, float easyChance, float normalChance, float hardChance) {
		this.peacefulChance = peacefulChance;
		this.defaultChance = defaultChance;
		this.easyChance = easyChance;
		this.normalChance = normalChance;
		this.hardChance = hardChance;
	}

	@Override
	public LootConditionType func_230419_b_() {
		return Registry.LOOT_CONDITION_TYPE.getOrDefault(ACLootConditions.RANDOM_DIFFICULTY_CHANCE);
	}

	@Override
	public boolean test(LootContext lootContext) {
		float chance = this.defaultChance;
		switch (lootContext.getWorld().getDifficulty()) {
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

	public static class Serializer implements ILootSerializer<RandomDifficultyChanceCondition> {
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
				return new RandomDifficultyChanceCondition(JSONUtils.getFloat(json, "default_chance"), getFloatOrMinus1(json, "peaceful"), getFloatOrMinus1(json, "easy"), getFloatOrMinus1(json, "normal"), getFloatOrMinus1(json, "hard"));
			}
			throw new JsonSyntaxException("Missing 'default_chance', expected to find a float");
		}

		private static float getFloatOrMinus1(JsonObject json, String fieldName) {
			if (json.has(fieldName))
				return JSONUtils.getFloat(json, fieldName);
			return -1;
		}
	}
}
