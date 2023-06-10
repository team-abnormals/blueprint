package core.data.server;

import com.teamabnormals.blueprint.common.world.modification.chunk.ChunkGeneratorModifierProvider;
import com.teamabnormals.blueprint.common.world.modification.chunk.modifiers.SurfaceRuleModifier;
import core.BlueprintTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.levelgen.SurfaceRules.ifTrue;
import static net.minecraft.world.level.levelgen.SurfaceRules.isBiome;
import static net.minecraft.world.level.levelgen.SurfaceRules.sequence;
import static net.minecraft.world.level.levelgen.SurfaceRules.state;

public final class TestChunkGeneratorModifiersProvider extends ChunkGeneratorModifierProvider {

	public TestChunkGeneratorModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(BlueprintTest.MOD_ID, output, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider lookupProvider) {
		this.entry("overworld_crimson_forest_surface_rule")
				.selects("minecraft:overworld")
				.addModifier(new SurfaceRuleModifier(ifTrue(isBiome(Biomes.CRIMSON_FOREST), sequence(ifTrue(SurfaceRules.ON_FLOOR, state(Blocks.CRIMSON_NYLIUM.defaultBlockState())), state(Blocks.NETHERRACK.defaultBlockState()))), false));
	}

}
