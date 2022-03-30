package core.data.server;

import com.teamabnormals.blueprint.common.world.modification.chunk.ChunkGeneratorModifierProvider;
import com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.SurfaceRuleModifier;
import com.teamabnormals.blueprint.core.registry.BlueprintSurfaceRules;
import core.BlueprintTest;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import static net.minecraft.world.level.levelgen.SurfaceRules.ifTrue;
import static net.minecraft.world.level.levelgen.SurfaceRules.isBiome;
import static net.minecraft.world.level.levelgen.SurfaceRules.sequence;
import static net.minecraft.world.level.levelgen.SurfaceRules.state;

public final class TestChunkGeneratorModifiersProvider extends ChunkGeneratorModifierProvider {

	public TestChunkGeneratorModifiersProvider(DataGenerator dataGenerator) {
		super(dataGenerator, BlueprintTest.MOD_ID);
	}

	@Override
	protected void registerEntries() {
		this.entry("overworld_crimson_forest_surface_rule")
				.selects("minecraft:overworld")
				.addModifier(new SurfaceRuleModifier(ifTrue(new BlueprintSurfaceRules.ModdednessSliceConditionSource(new ResourceLocation("blueprint_test:overworld_crimson_forest_caves")), ifTrue(isBiome(Biomes.CRIMSON_FOREST), sequence(ifTrue(SurfaceRules.ON_FLOOR, state(Blocks.CRIMSON_NYLIUM.defaultBlockState())), state(Blocks.NETHERRACK.defaultBlockState())))), false));
	}

}
