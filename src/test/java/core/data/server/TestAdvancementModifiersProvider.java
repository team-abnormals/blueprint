package core.data.server;

import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierProvider;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.*;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.ChoiceResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.MultiResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import core.BlueprintTest;
import core.registry.TestTriggers;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Optional.of;
import static java.util.Optional.empty;

public final class TestAdvancementModifiersProvider extends AdvancementModifierProvider {

	public TestAdvancementModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(BlueprintTest.MOD_ID, output, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider provider) {
		this.entry("all_potions")
				.selects("nether/all_potions")
				.addModifier(new EffectsChangedModifier("all_effects", false, MobEffectsPredicate.Builder.effects().and(MobEffects.BLINDNESS).build().get()));

		this.entry("balanced_diet")
				.selects("husbandry/balanced_diet")
				.addModifier(new ParentModifier(ResourceLocation.withDefaultNamespace("end/root")))
				.addModifier(new RewardsModifier(false, of(100000), of(List.of(BuiltInLootTables.JUNGLE_TEMPLE)), empty(), empty()))
				.addModifier(DisplayInfoModifier.builder().title(Component.translatable("blueprint_test.advancements.husbandry.balanced_diet.title")).description(Component.literal("Momma.")).type(AdvancementType.CHALLENGE).build())
				.addModifier(CriteriaModifier.builder(this.modId).addCriterion("test", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_SWORD)).requirements(AdvancementRequirements.Strategy.AND).build());

		this.entry("nether_displays")
				.selects("nether/distract_piglin", "nether/explore_nether", "nether/fast_travel")
				.addModifier(DisplayInfoModifier.builder().icon(new ItemStack(Items.NETHER_STAR)).build());

		this.entry("obtain_armor")
				.selects("story/obtain_armor")
				.addModifier(DisplayInfoModifier.builder().title(Component.translatable("blueprint_test.advancements.husbandry.obtain_armor.title")).description(Component.literal("Erm, yeah, environmental is loaded")).build(), new ModLoadedCondition("environmental"));

		this.entry("obtain_netherite_hoe")
				.selects("husbandry/obtain_netherite_hoe")
				.addModifier(CriteriaModifier.builder(this.modId).addCriterion("test", TestTriggers.EMPTY_TEST.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()))).requirements(AdvancementRequirements.Strategy.AND).shouldReplaceRequirements(true).build());

		this.entry("story_stuff")
				.selector(
						new MultiResourceSelector(
								new ChoiceResourceSelector(new NamesResourceSelector("story/mine_diamond"), new NamesResourceSelector("story/mine_stone"), new ModLoadedCondition(Blueprint.MOD_ID)),
								new NamesResourceSelector("story/root")
						)
				)
				.addModifier(DisplayInfoModifier.builder().description(Component.literal("Get modified!")).build());

		this.entry("tactical_fishing")
				.selects("husbandry/tactical_fishing")
				.addModifier(CriteriaModifier.builder(this.modId).addCriterion("test", FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.LAVA_BUCKET))).addIndexedRequirements(0, false, "test").build());
	}

}
