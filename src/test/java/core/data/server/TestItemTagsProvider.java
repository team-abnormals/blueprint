package core.data.server;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import core.BlueprintTest;
import core.registry.TestItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public final class TestItemTagsProvider extends ItemTagsProvider {

	public TestItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> tagLookup, ExistingFileHelper fileHelper) {
		super(packOutput, lookupProvider, tagLookup, BlueprintTest.MOD_ID, fileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.copy(BlueprintBlockTags.NOTE_BLOCK_TOP_INSTRUMENTS, ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS);
		this.tag(ItemTags.TRIM_TEMPLATES).add(TestItems.PRIMAL_ARMOR_TRIM_SMITHING_TEMPLATE.get());
		this.tag(ItemTags.TRIM_MATERIALS).add(Items.FLOWERING_AZALEA);
	}

}