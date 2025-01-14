package core.data.server;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.common.remolder.data.RemolderProvider;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.RegexResourceSelector;
import core.BlueprintTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.*;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.*;

public final class TestDataRemolderProvider extends RemolderProvider {

	public TestDataRemolderProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(BlueprintTest.MOD_ID, PackOutput.Target.DATA_PACK, packOutput, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider provider) {
		// Loot Modifiers
		// TODO: Replace with helper methods
		var pool = LootPool.lootPool().name("blueprint_test:chicken").setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.DIRT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))).when(LootItemKilledByPlayerCondition.killedByPlayer()).build();
		this.entry("loot/chicken")
			.path("minecraft:loot_table/entities/chicken")
			.remolder(add(
				target("pools[]"),
				value(pool, LootPool.CODEC)
			));
		var container = LootItem.lootTableItem(Items.NETHER_STAR).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(10).build();
		this.entry("loot/igloo_chest")
			.path("minecraft:loot_table/chests/igloo_chest")
			.remolder(add(
				target("pools[1].entries[]"),
				value(container, LootPoolEntries.CODEC)
			));
		container = LootItem.lootTableItem(Items.NETHERITE_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(5).build();
		this.entry("loot/vanilla_chests")
			.path(new RegexResourceSelector(Pattern.compile("minecraft:chests\\/.+")))
			.remolder(add(
				target("pools[0].entries[]"),
				value(container, LootPoolEntries.CODEC)
			));

		this.entry("piss_ocean")
				.path("minecraft:worldgen/biome/ocean", "minecraft:worldgen/biome/beach")
				.remolder(replace(
						target("effects.water_color"),
						value(16776960, Codec.INT)
				));
		this.entry("recipe_gold_block_to_netherite_block")
				.path("minecraft:recipes/gold_block")
				.remolder(replace(
						target("result.item"),
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
