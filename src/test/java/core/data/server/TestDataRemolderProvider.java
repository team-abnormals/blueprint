package core.data.server;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.common.remolder.data.RemolderProvider;
import core.BlueprintTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.CompletableFuture;

import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.add;
import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.replace;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.target;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.value;

public final class TestDataRemolderProvider extends RemolderProvider {

	public TestDataRemolderProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(BlueprintTest.MOD_ID, PackOutput.Target.DATA_PACK, packOutput, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider provider) {
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
