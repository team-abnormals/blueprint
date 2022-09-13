package core.data.server;

import com.teamabnormals.blueprint.common.world.modification.structure.SimpleStructureRepaletter;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletterProvider;
import com.teamabnormals.blueprint.common.world.modification.structure.WeightedStructureRepaletter;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import core.BlueprintTest;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.eventbus.api.EventPriority;

public final class TestStructureRepaletterProvider extends StructureRepaletterProvider {

	public TestStructureRepaletterProvider(DataGenerator dataGenerator) {
		super(dataGenerator, BlueprintTest.MOD_ID);
	}

	@Override
	protected void registerRepaletters() {
		this.registerRepaletter("planks_become_random_planks_in_mineshafts", new WeightedStructureRepaletter(BlockTags.PLANKS, WeightedRandomList.create(WeightedEntry.wrap(Blocks.ACACIA_PLANKS, 1), WeightedEntry.wrap(Blocks.BIRCH_PLANKS, 1))), BuiltinStructures.MINESHAFT.location());
		this.registerRepaletter("fences_become_random_fences_in_mineshafts", new WeightedStructureRepaletter(BlockTags.WOODEN_FENCES, WeightedRandomList.create(WeightedEntry.wrap(Blocks.CRIMSON_FENCE, 1), WeightedEntry.wrap(Blocks.WARPED_FENCE, 1))), BuiltinStructures.MINESHAFT.location());
		this.registerRepaletter("mossy_bricks_become_slime_blocks_in_cold_ocean_ruins", new ConditionedResourceSelector(new NamesResourceSelector(BuiltinStructures.OCEAN_RUIN_COLD.location()), new ModLoadedCondition(BlueprintTest.MOD_ID)), EventPriority.HIGHEST, new SimpleStructureRepaletter(Blocks.MOSSY_STONE_BRICKS, Blocks.SLIME_BLOCK));
		this.registerRepaletter("cobblestone_becomes_mossy_cobblestone_in_pillager_outposts", new SimpleStructureRepaletter(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE), BuiltinStructures.PILLAGER_OUTPOST.location());
		this.registerRepaletter("acacia_planks_become_copper_blocks_in_savanna_villages", new SimpleStructureRepaletter(Blocks.ACACIA_PLANKS, Blocks.COPPER_BLOCK), BuiltinStructures.VILLAGE_SAVANNA.location());
	}

}
