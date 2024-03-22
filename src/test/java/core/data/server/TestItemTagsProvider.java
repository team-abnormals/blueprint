package core.data.server;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags;
import core.BlueprintTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public final class TestItemTagsProvider extends ItemTagsProvider {

	public TestItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> tagLookup, ExistingFileHelper fileHelper) {
		super(packOutput, lookupProvider, tagLookup, BlueprintTest.MOD_ID, fileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.copy(BlueprintBlockTags.NOTE_BLOCK_TOP_INSTRUMENTS, ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS);
		this.tag(BlueprintItemTags.CHICKEN_FOOD).add(Items.HUSK_SPAWN_EGG);
		this.tag(BlueprintItemTags.PIG_FOOD).add(Items.CHIPPED_ANVIL);
		this.tag(BlueprintItemTags.STRIDER_FOOD).add(Items.MINECART);
		this.tag(BlueprintItemTags.STRIDER_TEMPT_ITEMS).add(Items.SPYGLASS);
		this.tag(BlueprintItemTags.OCELOT_FOOD).add(Items.DIRT, Items.DIRT_PATH);
		this.tag(BlueprintItemTags.CAT_FOOD).add(Items.APPLE);
	}

}