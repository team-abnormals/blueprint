package core.data.server;

import com.teamabnormals.blueprint.common.loot.modification.LootModifierProvider;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.LootPoolEntriesModifier;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.LootPoolsModifier;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.RegexResourceSelector;
import core.BlueprintTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class TestLootModifiersProvider extends LootModifierProvider {

	public TestLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(BlueprintTest.MOD_ID, output, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider lookupProvider) {
		this.entry("chicken")
				.selects("entities/chicken")
				.addModifier(new LootPoolsModifier(List.of(LootPool.lootPool().name("blueprint_test:chicken").setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.DIRT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))).when(LootItemKilledByPlayerCondition.killedByPlayer()).build()), true));

		this.entry("igloo_chest")
				.selects("chests/igloo_chest")
				.addModifier(new LootPoolEntriesModifier(false, 1, LootItem.lootTableItem(Items.NETHER_STAR).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(10).build()), new ModLoadedCondition(Blueprint.MOD_ID));

		this.entry("vanilla_chests")
				.selector(new RegexResourceSelector(Pattern.compile("minecraft:chests\\/.+")))
				.addModifier(new LootPoolEntriesModifier(false, 0, LootItem.lootTableItem(Items.NETHERITE_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(5).build()));
	}

}
