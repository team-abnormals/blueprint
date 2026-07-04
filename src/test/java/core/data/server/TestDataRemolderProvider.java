package core.data.server;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.CriteriaModifier;
import com.teamabnormals.blueprint.common.remolder.data.RemolderProvider;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.ChoiceResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.MultiResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.RegexResourceSelector;
import core.BlueprintTest;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.add;
import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.replace;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.target;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.value;
import static com.teamabnormals.blueprint.common.remolder.util.LootRemolders.addEntry;
import static com.teamabnormals.blueprint.common.remolder.util.LootRemolders.addPool;
import static com.teamabnormals.blueprint.common.remolder.util.AdvancementRemolders.*;

public final class TestDataRemolderProvider extends RemolderProvider {

	public TestDataRemolderProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(BlueprintTest.MOD_ID, PackOutput.Target.DATA_PACK, packOutput, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider provider) {
		// Loot Modifiers
		var pool = LootPool.lootPool().name("blueprint_test:chicken").setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.DIRT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))).when(LootItemKilledByPlayerCondition.killedByPlayer()).build();
		this.entry("loot/chicken")
				.path("minecraft:loot_table/entities/chicken")
				.remolder(addPool(pool));
		var container = LootItem.lootTableItem(Items.NETHER_STAR).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(10).build();
		this.entry("loot/igloo_chest")
				.priority(999)
				.path("minecraft:loot_table/chests/igloo_chest")
				.remolder(addEntry(1, container));
		container = LootItem.lootTableItem(Items.NETHERITE_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(5).build();
		this.entry("loot/vanilla_chests")
				.path(new RegexResourceSelector(Pattern.compile("minecraft:loot_table\\/chests\\/.+")))
				.remolder(addEntry(0, container));

		// Advancement Modifiers
		this.entry("advancement/all_potions")
				.path("minecraft:advancement/nether/all_potions")
				.remolder(addToEffectsChanged("all_effects", MobEffectsPredicate.Builder.effects().and(MobEffects.BLINDNESS).build().get()));
		this.entry("advancement/balanced_diet")
				.path("minecraft:advancement/husbandry/balanced_diet")
				.remolder(
						replaceParent(ResourceLocation.withDefaultNamespace("end/root")),
						remoldRewards(100000, List.of(BuiltInLootTables.JUNGLE_TEMPLE), null, null),
						remoldDisplayInfo().title(Component.translatable("blueprint_test.advancements.husbandry.balanced_diet.title")).description(Component.literal("Momma.")).type(AdvancementType.CHALLENGE).build(),
						criteria(CriteriaModifier.builder(this.modId).addCriterion("test", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_SWORD)).requirements(AdvancementRequirements.Strategy.AND).build())
				);
		this.entry("advancement/nether_displays")
				.path("minecraft:advancement/nether/distract_piglin", "minecraft:advancement/nether/explore_nether", "minecraft:advancement/nether/fast_travel")
				.remolder(remoldDisplayInfo().icon(new ItemStack(Items.NETHER_STAR)).build());
		this.entry("advancement/obtain_armor")
				.path(new ConditionedResourceSelector(new NamesResourceSelector("minecraft:advancement/story/obtain_armor"), new ModLoadedCondition("environmental")))
				.remolder(remoldDisplayInfo().title(Component.translatable("blueprint_test.advancements.husbandry.obtain_armor.title")).description(Component.literal("Erm, yeah, environmental is loaded")).build());
		this.entry("advancement/story_stuff")
				.path(
						new MultiResourceSelector(
								new ChoiceResourceSelector(new NamesResourceSelector("minecraft:advancement/story/mine_diamond"), new NamesResourceSelector("minecraft:advancement/story/mine_stone"), new ModLoadedCondition(Blueprint.MOD_ID)),
								new NamesResourceSelector("minecraft:advancement/story/root")
						)
				)
				.remolder(remoldDisplayInfo().description(Component.literal("Get modified!")).build());
		this.entry("advancement/tactical_fishing")
				.path("minecraft:advancement/husbandry/tactical_fishing")
				.remolder(criteria(CriteriaModifier.builder(this.modId).addCriterion("test", FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.LAVA_BUCKET))).addIndexedRequirements(0, false, "test").build()));

		this.entry("piss_ocean")
				.path("minecraft:worldgen/biome/ocean", "minecraft:worldgen/biome/beach")
				.remolder(replace(
						target("effects.water_color"),
						value(16776960, Codec.INT)
				));
		this.entry("bastion_indicators")
				.path("minecraft:worldgen/biome/plains", "minecraft:worldgen/biome/forest", "minecraft:worldgen/biome/dark_forest", "minecraft:worldgen/biome/desert", "minecraft:worldgen/biome/crimson_forest")
				.remolder(add(
						target("features[9][]"),
						value("blueprint_test:test_data_receiver", Codec.STRING)
				));
		this.entry("recipe_gold_block_to_netherite_block")
				.path("minecraft:recipe/gold_block")
				.remolder(replace(
						target("result.id"),
						value("minecraft:netherite_block", Codec.STRING)
				));
		// Remolder is not needed to do this, but this is just a test!
		this.entry("add_stone_to_wool_tag")
				.path("minecraft:tags/blocks/wool")
				.remolder(add(
						target("values[]"),
						value("minecraft:stone", Codec.STRING)
				));
	}

}
