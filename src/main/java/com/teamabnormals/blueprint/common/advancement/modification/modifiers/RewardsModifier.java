package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An {@link AdvancementModifier} implementation that modifies the rewards of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record RewardsModifier(Mode mode, Optional<Integer> experience, Optional<List<ResourceLocation>> loot, Optional<List<ResourceLocation>> recipes, Optional<ResourceLocation> function) implements AdvancementModifier<RewardsModifier> {

	private static JsonArray serializeResourceList(List<ResourceLocation> resources) {
		JsonArray array = new JsonArray();
		resources.forEach(resourceLocation -> array.add(resourceLocation.toString()));
		return array;
	}

	private static Optional<List<ResourceLocation>> deserializeResourceList(JsonObject object, String key) {
		if (GsonHelper.isValidNode(object, key)) {
			List<ResourceLocation> resourceLocations = Lists.newArrayList();
			object.getAsJsonArray(key).forEach(element -> resourceLocations.add(new ResourceLocation(element.getAsString())));
			return Optional.of(resourceLocations);
		}
		return Optional.empty();
	}

	@Override
	public void modify(Advancement.Builder builder) {
		if (this.mode == Mode.MODIFY) {
			AdvancementRewards rewards = builder.rewards;
			AdvancementRewards.Builder rewardsBuilder = new AdvancementRewards.Builder();
			rewardsBuilder.addExperience(rewards.experience);
			rewardsBuilder.loot.addAll(Arrays.asList(rewards.loot));
			for (ResourceLocation recipe : rewards.recipes) {
				rewardsBuilder.addRecipe(recipe);
			}
			rewardsBuilder.function = rewards.function.getId();

			this.experience.ifPresent(rewardsBuilder::addExperience);
			this.loot.ifPresent(rewardsBuilder.loot::addAll);
			this.recipes.ifPresent(rewardsBuilder.recipes::addAll);
			this.function.ifPresent(function -> rewardsBuilder.function = function);
			builder.rewards(rewardsBuilder);
		} else {
			AdvancementRewards.Builder rewardsBuilder = new AdvancementRewards.Builder();
			this.experience.ifPresent(rewardsBuilder::addExperience);
			this.loot.ifPresent(loot -> {
				rewardsBuilder.loot.clear();
				rewardsBuilder.loot.addAll(loot);
			});
			this.recipes.ifPresent(recipes -> {
				rewardsBuilder.recipes.clear();
				rewardsBuilder.recipes.addAll(recipes);
			});
			this.function.ifPresent(function -> rewardsBuilder.function = function);
			builder.rewards(rewardsBuilder);
		}
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.REWARDS;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<RewardsModifier> {
		@Override
		public JsonElement serialize(RewardsModifier modifier, Void additional) throws JsonParseException {
			JsonObject object = new JsonObject();
			modifier.mode.serialize(object);
			modifier.experience.ifPresent(experience -> object.addProperty("experience", experience));
			modifier.loot.ifPresent(loot -> object.add("loot", serializeResourceList(loot)));
			modifier.recipes.ifPresent(recipes -> object.add("recipes", serializeResourceList(recipes)));
			modifier.function.ifPresent(function -> object.addProperty("function", function.toString()));
			return object;
		}

		@Override
		public RewardsModifier deserialize(JsonElement element, DeserializationContext additional) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			Mode mode = Mode.deserialize(object);
			Optional<Integer> experience = GsonHelper.isValidNode(object, "experience") ? Optional.of(GsonHelper.getAsInt(object, "experience")) : Optional.empty();
			Optional<List<ResourceLocation>> loot = deserializeResourceList(object, "loot");
			Optional<List<ResourceLocation>> recipes = deserializeResourceList(object, "recipes");
			Optional<ResourceLocation> function = GsonHelper.isValidNode(object, "function") ? Optional.of(new ResourceLocation(GsonHelper.getAsString(object, "function"))) : Optional.empty();
			return new RewardsModifier(mode, experience, loot, recipes, function);
		}
	}

}
