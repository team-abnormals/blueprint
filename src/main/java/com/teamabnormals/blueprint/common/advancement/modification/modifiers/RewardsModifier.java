package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

/**
 * An {@link AdvancementModifier} implementation that modifies the rewards of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record RewardsModifier(boolean replaces, Optional<Integer> experience, Optional<List<ResourceKey<LootTable>>> loot, Optional<List<ResourceLocation>> recipes, Optional<ResourceLocation> function) implements AdvancementModifier<RewardsModifier> {
	@Override
	public void modify(Advancement.Builder builder) {
		if (!this.replaces) {
			AdvancementRewards rewards = builder.rewards;
			AdvancementRewards.Builder rewardsBuilder = new AdvancementRewards.Builder();
			rewardsBuilder.addExperience(this.experience.orElse(rewards.experience()));
			rewards.loot().forEach(rewardsBuilder::addLootTable);
			rewards.recipes().forEach(rewardsBuilder::addRecipe);
			this.loot.ifPresent(loot -> loot.forEach(rewardsBuilder::addLootTable));
			this.recipes.ifPresent(recipes -> recipes.forEach(rewardsBuilder::addRecipe));
			if (this.function.isPresent()) {
				rewardsBuilder.runs(this.function.get());
			} else if (rewards.function().isPresent()) {
				rewardsBuilder.runs(rewards.function().get().getId());
			}
			builder.rewards(rewardsBuilder);
		} else {
			AdvancementRewards.Builder rewardsBuilder = new AdvancementRewards.Builder();
			this.experience.ifPresent(rewardsBuilder::addExperience);
			this.loot.ifPresent(loot -> loot.forEach(rewardsBuilder::addLootTable));
			this.recipes.ifPresent(recipes -> recipes.forEach(rewardsBuilder::addRecipe));
			this.function.ifPresent(rewardsBuilder::runs);
			builder.rewards(rewardsBuilder);
		}
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.REWARDS;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<RewardsModifier> {
		private static final Codec<RewardsModifier> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.BOOL.optionalFieldOf("replaces", false).forGetter(RewardsModifier::replaces),
						Codec.INT.optionalFieldOf("experience").forGetter(RewardsModifier::experience),
						ResourceKey.codec(Registries.LOOT_TABLE).listOf().optionalFieldOf("loot").forGetter(RewardsModifier::loot),
						ResourceLocation.CODEC.listOf().optionalFieldOf("recipes").forGetter(RewardsModifier::recipes),
						ResourceLocation.CODEC.optionalFieldOf("function").forGetter(RewardsModifier::function)
				).apply(instance, RewardsModifier::new)
		);

		@Override
		public JsonElement serialize(RewardsModifier modifier, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.encodeStart(ops, modifier);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get();
		}

		@Override
		public RewardsModifier deserialize(JsonElement element, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.decode(ops, element);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get().getFirst();
		}
	}
}
