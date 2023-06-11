package core.data.server;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlice;
import com.teamabnormals.blueprint.common.world.modification.structure.SimpleStructureRepaletter;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletterEntry;
import com.teamabnormals.blueprint.common.world.modification.structure.WeightedStructureRepaletter;
import com.teamabnormals.blueprint.core.data.server.BlueprintDatapackBuiltinEntriesProvider;
import com.teamabnormals.blueprint.core.registry.BlueprintBiomes;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import core.BlueprintTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class TestDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {
	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.BIOME, BlueprintDatapackBuiltinEntriesProvider::bootstrapBiomes).add(BlueprintDataPackRegistries.STRUCTURE_REPALETTERS, TestDatapackBuiltinEntriesProvider::bootstrapStructureRepaletters).add(BlueprintDataPackRegistries.MODDED_BIOME_SLICES, TestDatapackBuiltinEntriesProvider::bootstrapSlices);

	public TestDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(BlueprintTest.MOD_ID));
	}

	private static void bootstrapStructureRepaletters(BootstapContext<StructureRepaletterEntry> context) {
		var structures = context.lookup(Registries.STRUCTURE);
		context.register(
				repaletterKey("planks_become_random_planks_in_mineshafts"),
				new StructureRepaletterEntry(
						HolderSet.direct(structures.getOrThrow(BuiltinStructures.MINESHAFT)),
						new WeightedStructureRepaletter(BlockTags.PLANKS, WeightedRandomList.create(WeightedEntry.wrap(Blocks.ACACIA_PLANKS, 1), WeightedEntry.wrap(Blocks.BIRCH_PLANKS, 1)))
				)
		);
		context.register(
				repaletterKey("fences_become_random_fences_in_mineshafts"),
				new StructureRepaletterEntry(
						HolderSet.direct(structures.getOrThrow(BuiltinStructures.MINESHAFT)),
						50,
						new WeightedStructureRepaletter(BlockTags.WOODEN_FENCES, WeightedRandomList.create(WeightedEntry.wrap(Blocks.CRIMSON_FENCE, 1), WeightedEntry.wrap(Blocks.WARPED_FENCE, 1)))
				)
		);
		context.register(
				repaletterKey("mossy_bricks_become_slime_blocks_in_cold_ocean_ruins"),
				new StructureRepaletterEntry(
						HolderSet.direct(structures.getOrThrow(BuiltinStructures.OCEAN_RUIN_COLD)),
						0,
						new SimpleStructureRepaletter(Blocks.MOSSY_STONE_BRICKS, Blocks.SLIME_BLOCK)
				)
		);
		context.register(
				repaletterKey("cobblestone_becomes_mossy_cobblestone_in_pillager_outposts"),
				new StructureRepaletterEntry(
						HolderSet.direct(structures.getOrThrow(BuiltinStructures.PILLAGER_OUTPOST)),
						new SimpleStructureRepaletter(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE)
				)
		);
		context.register(
				repaletterKey("acacia_planks_become_copper_blocks_in_savanna_villages"),
				new StructureRepaletterEntry(
						HolderSet.direct(structures.getOrThrow(BuiltinStructures.VILLAGE_SAVANNA)),
						new SimpleStructureRepaletter(Blocks.ACACIA_PLANKS, Blocks.COPPER_BLOCK)
				)
		);
	}

	private static void bootstrapSlices(BootstapContext<ModdedBiomeSlice> context) {
		var levels = context.lookup(Registries.LEVEL_STEM);
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
		context.register(
				sliceKey("nether_test"),
				new ModdedBiomeSlice(
						40,
						new BiomeUtil.MultiNoiseModdedBiomeProvider(new Climate.ParameterList<>(List.of(Pair.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0), biomes.getOrThrow(Biomes.FOREST))))),
						LevelStem.NETHER
				)
		);
		Climate.Parameter zero = Climate.Parameter.point(0.0F);
		context.register(
				sliceKey("overworld_crimson_forest_caves"),
				new ModdedBiomeSlice(
						40,
						new BiomeUtil.MultiNoiseModdedBiomeProvider(new Climate.ParameterList<>(List.of(Pair.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0), biomes.getOrThrow(BlueprintBiomes.ORIGINAL_SOURCE_MARKER)), Pair.of(Climate.parameters(zero, zero, zero, zero, Climate.Parameter.span(0.3F, 1.0F), zero, 0.0F), biomes.getOrThrow(Biomes.CRIMSON_FOREST))))),
						LevelStem.OVERWORLD
				)
		);
	}

	private static ResourceKey<StructureRepaletterEntry> repaletterKey(String name) {
		return ResourceKey.create(BlueprintDataPackRegistries.STRUCTURE_REPALETTERS, new ResourceLocation(BlueprintTest.MOD_ID, name));
	}

	private static ResourceKey<ModdedBiomeSlice> sliceKey(String name) {
		return ResourceKey.create(BlueprintDataPackRegistries.MODDED_BIOME_SLICES, new ResourceLocation(BlueprintTest.MOD_ID, name));
	}
}
