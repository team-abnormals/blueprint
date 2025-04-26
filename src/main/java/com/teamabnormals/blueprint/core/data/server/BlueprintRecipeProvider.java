package com.teamabnormals.blueprint.core.data.server;

import com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlueprintRecipeProvider extends RecipeProvider {
	private final String modid;

	public BlueprintRecipeProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider);
		this.modid = modid;
	}

	@Override
	public void buildRecipes(RecipeOutput output) {
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Blocks.CAKE).define('A', Tags.Items.BUCKETS_MILK).define('B', Items.SUGAR).define('C', Items.WHEAT).define('E', Tags.Items.EGGS).pattern("AAA").pattern("BEB").pattern("CCC").unlockedBy("has_egg", has(Tags.Items.EGGS)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PUMPKIN_PIE).requires(BlueprintItemTags.PUMPKINS).requires(Items.SUGAR).requires(Tags.Items.EGGS).unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN)).unlockedBy("has_pumpkin", has(BlueprintItemTags.PUMPKINS)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PUMPKIN_SEEDS, 4).requires(BlueprintItemTags.PUMPKINS).unlockedBy("has_pumpkin", has(BlueprintItemTags.PUMPKINS)).save(output);
	}

	public static void foodCookingRecipes(RecipeOutput recipeOutput, ItemLike input, ItemLike output) {
		foodCookingRecipes(recipeOutput, input, output, 0.35F, 200);
	}

	public static void foodCookingRecipes(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float xp, int baseCookTime) {
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, output, xp, baseCookTime).unlockedBy(getHasName(input), has(input)).save(recipeOutput);
		SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, output, xp, baseCookTime / 2).unlockedBy(getHasName(input), has(input)).save(recipeOutput, RecipeBuilder.getDefaultRecipeId(output) + "_from_smoking");
		SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, output, xp, baseCookTime * 3).unlockedBy(getHasName(input), has(input)).save(recipeOutput, RecipeBuilder.getDefaultRecipeId(output) + "_from_campfire_cooking");
	}

	public void oreRecipes(RecipeOutput recipeOutput, List<ItemLike> inputs, RecipeCategory category, ItemLike output, float smeltingXp, int smeltingTime, String group) {
		this.oreRecipes(recipeOutput, inputs, category, output, smeltingXp, smeltingTime, smeltingXp, smeltingTime / 2, group);
	}

	public void oreRecipes(RecipeOutput recipeOutput, List<ItemLike> inputs, RecipeCategory category, ItemLike output, float smeltingXp, int smeltingTime, float blastingXp, int blastingTime, String group) {
		this.smeltingRecipe(recipeOutput, inputs, category, output, smeltingXp, smeltingTime, group);
		this.blastingRecipe(recipeOutput, inputs, category, output, blastingXp, blastingTime, group);
	}

	public void smeltingRecipe(RecipeOutput recipeOutput, List<ItemLike> inputs, RecipeCategory category, ItemLike output, float xp, int cookTime, String group) {
		for (ItemLike item : inputs) {
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(item), category, output, xp, cookTime)
					.unlockedBy(getHasName(item), has(item))
					.group(group)
					.save(recipeOutput);
		}
	}

	public void blastingRecipe(RecipeOutput recipeOutput, List<ItemLike> inputs, RecipeCategory category, ItemLike output, float xp, int cookTime, String group) {
		for (ItemLike item : inputs) {
			SimpleCookingRecipeBuilder.blasting(Ingredient.of(item), category, output, xp, cookTime)
					.unlockedBy(getHasName(item), has(item))
					.group(group)
					.save(recipeOutput);
		}
	}

	public void leafPileRecipes(RecipeOutput output, ItemLike leaves, ItemLike leafPile) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, leafPile, 4).requires(leaves).group("leaf_pile").unlockedBy(getHasName(leaves), has(leaves)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, leaves).define('#', leafPile).pattern("##").pattern("##").group("leaves").unlockedBy(getHasName(leafPile), has(leafPile)).save(output, this.getModConversionRecipeName(leaves, leafPile));
	}

	public void stonecutterRecipe(RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, ItemLike input) {
		this.stonecutterRecipe(recipeOutput, category, output, input, 1);
	}

	public void stonecutterRecipe(RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, ItemLike input, int count) {
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), category, output, count).unlockedBy(getHasName(input), has(input)).save(recipeOutput, this.getModConversionRecipeName(output, input) + "_stonecutting");
	}

	public void conversionRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
		this.conversionRecipe(recipeOutput, output, input, null);
	}

	public void conversionRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input, @Nullable String group) {
		this.conversionRecipe(recipeOutput, output, input, group, 1);
	}

	public void conversionRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input, @Nullable String group, int count) {
		conversionRecipeBuilder(output, input, count).group(group).save(recipeOutput, this.getModConversionRecipeName(output, input));
	}

	public static ShapelessRecipeBuilder conversionRecipeBuilder(ItemLike output, ItemLike input, int count) {
		return conversionRecipeBuilder(RecipeCategory.MISC, output, input, count);
	}

	public static ShapelessRecipeBuilder conversionRecipeBuilder(RecipeCategory category, ItemLike output, ItemLike input, int count) {
		return ShapelessRecipeBuilder.shapeless(category, output, count).requires(input).unlockedBy(getHasName(input), has(input));
	}

	public void storageRecipes(RecipeOutput output, RecipeCategory itemCategory, ItemLike item, RecipeCategory storageCategory, ItemLike storage, String storageName, String storageGroup, String itemName, String itemGroup) {
		nineBlockStorageRecipes(output, itemCategory, item, storageCategory, storage, this.modid + ":" + storageName, storageGroup, this.modid + ":" + itemName, itemGroup);
	}

	public void storageRecipes(RecipeOutput output, RecipeCategory itemCategory, ItemLike item, RecipeCategory storageCategory, ItemLike storage) {
		storageRecipes(output, itemCategory, item, storageCategory, storage, getSimpleRecipeName(storage), null, getSimpleRecipeName(item), null);
	}

	public void storageRecipesWithCustomPacking(RecipeOutput output, RecipeCategory itemCategory, ItemLike item, RecipeCategory storageCategory, ItemLike storage, String storageName, String storageGroup) {
		storageRecipes(output, itemCategory, item, storageCategory, storage, storageName, storageGroup, getSimpleRecipeName(item), null);
	}

	public void storageRecipesWithCustomUnpacking(RecipeOutput output, RecipeCategory itemCategory, ItemLike item, RecipeCategory storageCategory, ItemLike storage, String itemName, String itemGroup) {
		storageRecipes(output, itemCategory, item, storageCategory, storage, getSimpleRecipeName(storage), null, itemName, itemGroup);
	}

	public void conditionalStorageRecipes(RecipeOutput output, ICondition condition, RecipeCategory itemCategory, ItemLike item, RecipeCategory storageCategory, ItemLike storage) {
		conditionalStorageRecipes(output, condition, itemCategory, item, storageCategory, storage, getSimpleRecipeName(storage), null, getSimpleRecipeName(item), null);
	}

	public void conditionalStorageRecipes(RecipeOutput output, ICondition condition, RecipeCategory itemCategory, ItemLike item, RecipeCategory storageCategory, ItemLike storage, String storageLocation, @Nullable String itemGroup, String itemLocation, @Nullable String storageGroup) {
		conditionalRecipe(output, ShapelessRecipeBuilder.shapeless(itemCategory, item, 9).requires(storage).group(storageGroup).unlockedBy(getHasName(storage), has(storage)), ResourceLocation.fromNamespaceAndPath(this.modid, itemLocation), condition);
		conditionalRecipe(output, ShapedRecipeBuilder.shaped(storageCategory, storage).define('#', item).pattern("###").pattern("###").pattern("###").group(itemGroup).unlockedBy(getHasName(item), has(item)), ResourceLocation.fromNamespaceAndPath(this.modid, storageLocation), condition);
	}

	public void conditionalStorageRecipesWithCustomUnpacking(RecipeOutput output, ICondition condition, RecipeCategory itemCategory, ItemLike item, RecipeCategory storageCategory, ItemLike storage, String shapelessName, String shapelessGroup) {
		conditionalStorageRecipes(output, condition, itemCategory, item, storageCategory, storage, getSimpleRecipeName(storage), null, shapelessName, shapelessGroup);
	}

	public static void conditionalRecipe(RecipeOutput output, RecipeBuilder recipe, ICondition... conditions) {
		recipe.save(output.withConditions(conditions));
	}

	public static void conditionalRecipe(RecipeOutput output, RecipeBuilder recipe, ResourceLocation id, ICondition... conditions) {
		recipe.save(output.withConditions(conditions), id);
	}

	public void waxRecipe(RecipeOutput output, RecipeCategory category, ItemLike input, ItemLike result) {
		ShapelessRecipeBuilder.shapeless(category, result).requires(input).requires(Items.HONEYCOMB).group(getItemName(result)).unlockedBy(getHasName(input), has(input)).save(output, this.getModConversionRecipeName(result, Items.HONEYCOMB));
	}

	public void netheriteSmithingRecipe(RecipeOutput recipeOutput, Item input, RecipeCategory category, Item output) {
		SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(input), Ingredient.of(Items.NETHERITE_INGOT), category, output).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(this.modid, getItemName(output) + "_smithing"));
	}

	public static void trimSmithing(RecipeOutput output, ItemLike item) {
		SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(item), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC).unlocks("has_smithing_trim_template", has(item)).save(output, suffix(RecipeBuilder.getDefaultRecipeId(item), "_smithing_trim"));
	}

	public static void trimRecipes(RecipeOutput output, ItemLike item, TagKey<Item> copyItem) {
		trimSmithing(output, item);
		copySmithingTemplate(output, item, copyItem);
	}

	public static void trimRecipes(RecipeOutput output, ItemLike item, ItemLike copyItem) {
		trimSmithing(output, item);
		copySmithingTemplate(output, item, copyItem);
	}

	public static void generateRecipes(RecipeOutput output, BlockFamily family) {
		generateRecipes(output, family, FeatureFlags.REGISTRY.allFlags());
	}

	public static ResourceLocation suffix(ResourceLocation rl, String suffix) {
		return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);
	}

	public ResourceLocation getModConversionRecipeName(ItemLike output, ItemLike input) {
		return ResourceLocation.fromNamespaceAndPath(this.modid, getConversionRecipeName(output, input));
	}

	public String getModID() {
		return this.modid;
	}
}