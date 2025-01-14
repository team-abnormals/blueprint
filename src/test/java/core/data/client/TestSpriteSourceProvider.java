package core.data.client;

import com.teamabnormals.blueprint.core.api.BlueprintTrims;
import core.BlueprintTest;
import core.registry.TestTrimMaterials;
import core.registry.TestTrimPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

public final class TestSpriteSourceProvider extends SpriteSourceProvider {

	public TestSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, BlueprintTest.MOD_ID, existingFileHelper);
	}

	@Override
	protected void gather() {
		this.atlas(BlueprintTrims.ARMOR_TRIMS_ATLAS)
			.addSource(BlueprintTrims.patternPermutationsOfVanillaMaterials(TestTrimPatterns.PRIMAL))
			.addSource(BlueprintTrims.materialPatternPermutations(TestTrimMaterials.TEST));
		this.atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(BlueprintTrims.materialPermutationsForItemLayers(TestTrimMaterials.TEST));
	}

}
