package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
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
	private static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Mode.CODEC.fieldOf("mode").forGetter(config -> config.mode),
				Codec.INT.optionalFieldOf("experience").forGetter(config -> config.experience),
				ResourceLocation.CODEC.listOf().optionalFieldOf("loot").forGetter(config -> config.loot),
				ResourceLocation.CODEC.listOf().optionalFieldOf("recipes").forGetter(config -> config.loot),
				ResourceLocation.CODEC.optionalFieldOf("function").forGetter(config -> config.function)
		).apply(instance, Config::new);
	});

	public RewardsModifier() {
		super(CODEC);
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
