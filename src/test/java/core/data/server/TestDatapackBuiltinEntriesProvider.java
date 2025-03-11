package core.data.server;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlice;
import com.teamabnormals.blueprint.common.world.modification.structure.SimpleStructureRepaletter;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletterEntry;
import com.teamabnormals.blueprint.common.world.modification.structure.WeightedStructureRepaletter;
import com.teamabnormals.blueprint.common.world.modification.structure.condition.AndStructureCondition;
import com.teamabnormals.blueprint.common.world.modification.structure.condition.BiomeStructureCondition;
import com.teamabnormals.blueprint.common.world.modification.structure.condition.ChanceStructureCondition;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.data.server.BlueprintDatapackBuiltinEntriesProvider;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import core.BlueprintTest;
import core.registry.TestTrimMaterials;
import core.registry.TestTrimPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletterEntry.repalette;

public final class TestDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {
	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.BIOME, BlueprintDatapackBuiltinEntriesProvider::bootstrapBiomes)
			.add(BlueprintDataPackRegistries.STRUCTURE_REPALETTERS, TestDatapackBuiltinEntriesProvider::bootstrapStructureRepaletters)
			.add(BlueprintDataPackRegistries.MODDED_BIOME_SLICES, TestDatapackBuiltinEntriesProvider::bootstrapSlices)
			.add(Registries.TRIM_MATERIAL, TestTrimMaterials::bootstrap)
			.add(Registries.TRIM_PATTERN, TestTrimPatterns::bootstrap);

	private static final ResourceKey<StructureRepaletterEntry> PLANKS_BECOME_RANDOM_PLANKS_IN_MINESHAFTS = repaletterKey("planks_become_random_planks_in_mineshafts");

	public TestDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, conditionsConsumer -> {
			conditionsConsumer.accept(PLANKS_BECOME_RANDOM_PLANKS_IN_MINESHAFTS, new ModLoadedCondition(Blueprint.MOD_ID));
		}, Set.of(BlueprintTest.MOD_ID));
	}

	private static void bootstrapStructureRepaletters(BootstrapContext<StructureRepaletterEntry> context) {
		var structures = context.lookup(Registries.STRUCTURE);
		var pieces = context.lookup(Registries.STRUCTURE_PIECE);
		context.register(
				PLANKS_BECOME_RANDOM_PLANKS_IN_MINESHAFTS,
				repalette()
					.repaletters(new WeightedStructureRepaletter(BlockTags.PLANKS, WeightedRandomList.create(WeightedEntry.wrap(Blocks.ACACIA_PLANKS, 1), WeightedEntry.wrap(Blocks.BIRCH_PLANKS, 1))))
					.select(HolderSet.direct(structures.getOrThrow(BuiltinStructures.MINESHAFT)))
		);
		var biomes = context.lookup(Registries.BIOME);
		context.register(
				repaletterKey("fences_become_random_fences_in_mineshafts"),
				repalette()
					.condition(AndStructureCondition.and(new ChanceStructureCondition(0.5F), new BiomeStructureCondition(HolderSet.direct(biomes.getOrThrow(Biomes.FOREST)))))
					.priority(50)
					.repaletters(new WeightedStructureRepaletter(BlockTags.WOODEN_FENCES, WeightedRandomList.create(WeightedEntry.wrap(Blocks.CRIMSON_FENCE, 1), WeightedEntry.wrap(Blocks.WARPED_FENCE, 1))))
					.select(HolderSet.direct(structures.getOrThrow(BuiltinStructures.MINESHAFT)))
		);
		context.register(
				repaletterKey("mossy_bricks_become_slime_blocks_in_cold_ocean_ruins"),
				repalette()
					.condition(new ChanceStructureCondition(0.5F))
					.priority(0)
					.repaletters(new SimpleStructureRepaletter(Blocks.MOSSY_STONE_BRICKS, Blocks.SLIME_BLOCK))
					.select(HolderSet.direct(structures.getOrThrow(BuiltinStructures.OCEAN_RUIN_COLD)))
		);
		context.register(
				repaletterKey("cobblestone_becomes_mossy_cobblestone_in_pillager_outposts"),
				repalette()
					.repaletters(new SimpleStructureRepaletter(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE))
					.select(HolderSet.direct(structures.getOrThrow(BuiltinStructures.PILLAGER_OUTPOST)))
		);
		context.register(
				repaletterKey("bookshelves_becomes_chiseled_bookshelves_in_stronghold_libraries"),
				repalette()
					.pieces(HolderSet.direct(pieces.getOrThrow(ResourceKey.create(Registries.STRUCTURE_PIECE, ResourceLocation.withDefaultNamespace("shli")))))
					.repaletters(new SimpleStructureRepaletter(Blocks.BOOKSHELF, Blocks.CHISELED_BOOKSHELF))
					.select(HolderSet.direct(structures.getOrThrow(BuiltinStructures.STRONGHOLD)))
		);
	}

	private static void bootstrapSlices(BootstrapContext<ModdedBiomeSlice> context) {
		var biomes = context.lookup(Registries.BIOME);
		context.register(
				sliceKey("end_checkerboard"),
				new ModdedBiomeSlice(
						50,
						new BiomeUtil.OverlayModdedBiomeProvider(
								List.of(
										Pair.of(
												biomes.getOrThrow(BiomeTags.HAS_END_CITY),
												new CheckerboardColumnBiomeSource(biomes.getOrThrow(BiomeTags.HAS_STRONGHOLD), 2)
										)
								)
						),
						LevelStem.END
				)
		);
		var parameterLists = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
		context.register(
				sliceKey("nether_test"),
				new ModdedBiomeSlice(
						40,
						BiomeUtil.MultiNoiseModdedBiomeProvider.builder()
								.area(Biomes.CRIMSON_FOREST, Biomes.FOREST)
								.biomes(parameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER))
								.build(),
						LevelStem.NETHER
				)
		);
		Climate.Parameter zero = Climate.Parameter.point(0.0F);
		context.register(
				sliceKey("overworld_crimson_forest_caves"),
				new ModdedBiomeSlice(
						40,
						BiomeUtil.MultiNoiseModdedBiomeProvider.builder()
								.biomes(consumer -> {
									consumer.accept(Pair.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0), BlueprintBiomes.ORIGINAL_SOURCE_MARKER));
									consumer.accept(Pair.of(Climate.parameters(zero, zero, zero, zero, Climate.Parameter.span(0.3F, 1.0F), zero, 0.0F), Biomes.CRIMSON_FOREST));
								})
								.onlyMapFromAreas(false)
								.build(),
						LevelStem.OVERWORLD
				)
		);
		context.register(
				sliceKey("ocean_small_end_islands"),
				new ModdedBiomeSlice(
						100,
						BiomeUtil.MultiNoiseModdedBiomeProvider.builder()
								.area(Biomes.OCEAN, Biomes.SMALL_END_ISLANDS)
								.biomes(parameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD))
								.build(),
						LevelStem.OVERWORLD
				)
		);
		// How well does the system perform having to cycle through 1000 invalid slices in areas that are not cold?
		// Answer: Decently! Slices go zoom zoom
		/*ResourceKey<Biome> coldStressTestAreaKey = ResourceKey.create(Registries.BIOME, new ResourceLocation(BlueprintTest.MOD_ID, "cold_stress_test"));
		for (int i = 0; i < 1000; i++) {
			context.register(
					sliceKey("cold_stress_test_" + i),
					new ModdedBiomeSlice(
							10,
							BiomeUtil.MultiNoiseModdedBiomeProvider.builder()
									.area(coldStressTestAreaKey, i % 2 == 0 ? Biomes.WARPED_FOREST : Biomes.BASALT_DELTAS)
									.biomes(consumer -> {
										consumer.accept(Pair.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0), BlueprintBiomes.ORIGINAL_SOURCE_MARKER));
										consumer.accept(Pair.of(Climate.parameters(Climate.Parameter.span(-1.0F, -0.45F), zero, zero, zero, zero, zero, 0.0F), coldStressTestAreaKey));
									})
									.build(),
							LevelStem.OVERWORLD
					)
			);
		}*/
	}

	private static ResourceKey<StructureRepaletterEntry> repaletterKey(String name) {
		return ResourceKey.create(BlueprintDataPackRegistries.STRUCTURE_REPALETTERS, ResourceLocation.fromNamespaceAndPath(BlueprintTest.MOD_ID, name));
	}

	private static ResourceKey<ModdedBiomeSlice> sliceKey(String name) {
		return ResourceKey.create(BlueprintDataPackRegistries.MODDED_BIOME_SLICES, ResourceLocation.fromNamespaceAndPath(BlueprintTest.MOD_ID, name));
	}
}
