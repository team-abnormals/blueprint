package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An {@link AdvancementModifier} extension that modifies the rewards of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RewardsModifier extends AdvancementModifier<RewardsModifier.Config> {

	public RewardsModifier() {
		super(((element, conditionArrayParser) -> {
			JsonObject object = element.getAsJsonObject();
			Mode mode = Mode.deserialize(object);
			Optional<Integer> experience = JSONUtils.hasField(object, "experience") ? Optional.of(JSONUtils.getInt(object, "experience")) : Optional.empty();
			Optional<List<ResourceLocation>> loot = deserializeResourceList(object, "loot");
			Optional<List<ResourceLocation>> recipes = deserializeResourceList(object, "recipes");
			Optional<ResourceLocation> function = JSONUtils.hasField(object, "function") ? Optional.of(new ResourceLocation(JSONUtils.getString(object, "function"))) : Optional.empty();
			return new Config(mode, experience, loot, recipes, function);
		}));
	}

	private static Optional<List<ResourceLocation>> deserializeResourceList(JsonObject object, String key) {
		if (JSONUtils.hasField(object, key)) {
			List<ResourceLocation> resourceLocations = Lists.newArrayList();
			object.getAsJsonArray(key).forEach(element -> resourceLocations.add(new ResourceLocation(element.getAsString())));
			return Optional.of(resourceLocations);
		}
		return Optional.empty();
	}

	@Override
	public void modify(Advancement.Builder builder, Config config) {
		if (config.mode == Mode.MODIFY) {
			AdvancementRewards rewards = builder.rewards;
			AdvancementRewards.Builder rewardsBuilder = new AdvancementRewards.Builder();
			rewardsBuilder.addExperience(rewards.experience);
			rewardsBuilder.loot.addAll(Arrays.asList(rewards.loot));
			for (ResourceLocation recipe : rewards.recipes) {
				rewardsBuilder.addRecipe(recipe);
			}
			rewardsBuilder.function = rewards.function.getId();

			config.experience.ifPresent(rewardsBuilder::addExperience);
			config.loot.ifPresent(rewardsBuilder.loot::addAll);
			config.recipes.ifPresent(rewardsBuilder.recipes::addAll);
			config.function.ifPresent(function -> rewardsBuilder.function = function);
			builder.withRewards(rewardsBuilder);
		} else {
			AdvancementRewards.Builder rewardsBuilder = new AdvancementRewards.Builder();
			config.experience.ifPresent(rewardsBuilder::addExperience);
			config.loot.ifPresent(loot -> {
				rewardsBuilder.loot.clear();
				rewardsBuilder.loot.addAll(loot);
			});
			config.recipes.ifPresent(recipes -> {
				rewardsBuilder.recipes.clear();
				rewardsBuilder.recipes.addAll(recipes);
			});
			config.function.ifPresent(function -> rewardsBuilder.function = function);
			builder.withRewards(rewardsBuilder);
		}
	}

	static class Config {
		private final Mode mode;
		private final Optional<Integer> experience;
		private final Optional<List<ResourceLocation>> loot;
		private final Optional<List<ResourceLocation>> recipes;
		private final Optional<ResourceLocation> function;

		Config(Mode mode, Optional<Integer> experience, Optional<List<ResourceLocation>> loot, Optional<List<ResourceLocation>> recipes, Optional<ResourceLocation> function) {
			this.mode = mode;
			this.experience = experience;
			this.loot = loot;
			this.recipes = recipes;
			this.function = function;
		}
	}

}
