package core.data.server;

import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierProvider;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.*;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.ChoiceResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.MultiResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import core.BlueprintTest;
import core.registry.TestTriggers;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.List;

import static java.util.Optional.of;
import static java.util.Optional.empty;

public final class TestAdvancementModifiersProvider extends AdvancementModifierProvider {

	public TestAdvancementModifiersProvider(DataGenerator dataGenerator) {
		super(dataGenerator, BlueprintTest.MOD_ID);
	}

	@Override
	protected void registerEntries() {
		this.entry("all_potions")
				.selects("nether/all_potions")
				.addModifier(new EffectsChangedModifier("all_effects", false, MobEffectsPredicate.effects().and(MobEffects.BLINDNESS)));

		this.entry("balanced_diet")
				.selects("husbandry/balanced_diet")
				.addModifier(new ParentModifier(new ResourceLocation("end/root")))
				.addModifier(new RewardsModifier(AdvancementModifier.Mode.MODIFY, of(100000), of(List.of(new ResourceLocation("chests/jungle_temple"))), empty(), empty()))
				.addModifier(DisplayInfoModifier.builder().title(Component.translatable("blueprint_test.advancements.husbandry.balanced_diet.title")).description(Component.literal("Momma.")).frame(FrameType.CHALLENGE).build())
				.addModifier(CriteriaModifier.builder(this.modId).addCriterion("test", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_SWORD)).requirements(RequirementsStrategy.AND).build());

		this.entry("nether_displays")
				.selects("nether/distract_piglin", "nether/explore_nether", "nether/fast_travel")
				.addModifier(DisplayInfoModifier.builder().icon(new ItemStack(Items.NETHER_STAR)).build());

		this.entry("obtain_armor")
				.selects("story/obtain_armor")
				.addModifier(DisplayInfoModifier.builder().title(Component.translatable("blueprint_test.advancements.husbandry.obtain_armor.title")).description(Component.literal("Erm, yeah, environmental is loaded")).build(), new ModLoadedCondition("environmental"));

		this.entry("obtain_netherite_hoe")
				.selects("husbandry/obtain_netherite_hoe")
				.addModifier(CriteriaModifier.builder(this.modId).addCriterion("test", TestTriggers.EMPTY_TEST.createInstance()).requirements(RequirementsStrategy.AND).shouldReplaceRequirements(true).build());

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
				.addModifier(CriteriaModifier.builder(this.modId).addCriterion("test", FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.LAVA_BUCKET).build())).addIndexedRequirements(0, false, "test").build());
	}

}
