package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags.*;

public class BlueprintItemTagsProvider extends ItemTagsProvider {

	public BlueprintItemTagsProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> tagLookup, ExistingFileHelper fileHelper) {
		super(output, lookupProvider, tagLookup, modid, fileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider p_256380_) {
		this.tag(MILK).addTag(Tags.Items.BUCKETS_MILK);
		this.tag(PUMPKINS).add(Items.PUMPKIN);
		this.tag(HORSE_ARMOR).add(Items.LEATHER_HORSE_ARMOR, Items.IRON_HORSE_ARMOR, Items.GOLDEN_HORSE_ARMOR, Items.DIAMOND_HORSE_ARMOR);

		this.copyWoodworksTags();
		this.copy(BlueprintBlockTags.LEAF_PILES, LEAF_PILES);

		this.copy(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
		this.copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
		this.copy(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES);
		this.copy(BlueprintBlockTags.LADDERS, LADDERS);

		this.tag(FURNACE_BOATS);
		this.tag(LARGE_BOATS);
	}

	public void copyWoodsetTags() {
		this.copyWoodenTags();
		this.copyWoodworksTags();
		this.copyLeavesTags();
	}

	public void copyWoodenTags() {
		this.copyWoodenTags(true);
	}

	public void copyWoodenTags(boolean flammable) {
		this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
		this.copy(!flammable ? BlockTags.LOGS : BlockTags.LOGS_THAT_BURN, !flammable ? ItemTags.LOGS : ItemTags.LOGS_THAT_BURN);
		this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
		this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);

		this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
	}

	public void copyWoodworksTags() {
		this.copy(BlueprintBlockTags.WOODEN_BOARDS, WOODEN_BOARDS);
		this.copy(BlueprintBlockTags.WOODEN_CHESTS, WOODEN_CHESTS);
		this.copy(BlueprintBlockTags.WOODEN_TRAPPED_CHESTS, WOODEN_TRAPPED_CHESTS);
		this.copy(BlueprintBlockTags.WOODEN_LADDERS, WOODEN_LADDERS);
		this.copy(BlueprintBlockTags.WOODEN_BEEHIVES, WOODEN_BEEHIVES);
		this.copy(BlueprintBlockTags.WOODEN_BOOKSHELVES, WOODEN_BOOKSHELVES);
		this.copy(BlueprintBlockTags.WOODEN_CHISELED_BOOKSHELVES, WOODEN_CHISELED_BOOKSHELVES);
	}

	public void copyLeavesTags() {
		this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
		this.copy(BlueprintBlockTags.LEAF_PILES, LEAF_PILES);
	}
}
