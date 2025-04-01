package com.teamabnormals.blueprint.core.data.server.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags.*;

public class BlueprintBlockTagsProvider extends BlockTagsProvider {

	public BlueprintBlockTagsProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
		super(output, provider, modid, helper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(ATTACHES_BLOCKS_TO_PISTON).add(Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK);
		this.tag(NOTE_BLOCK_TOP_INSTRUMENTS);

		this.tag(WOODEN_CHESTS);
		this.tag(WOODEN_TRAPPED_CHESTS);
		this.tag(WOODEN_BEEHIVES).add(Blocks.BEEHIVE);
		this.tag(WOODEN_LADDERS).add(Blocks.LADDER);
		this.tag(WOODEN_BOOKSHELVES).add(Blocks.BOOKSHELF);
		this.tag(WOODEN_CHISELED_BOOKSHELVES).add(Blocks.CHISELED_BOOKSHELF);

		this.tag(WOODEN_BOARDS);
		this.tag(LEAF_PILES);

		this.tag(BlockTags.MINEABLE_WITH_AXE).addTags(WOODEN_BOARDS, WOODEN_BOOKSHELVES, WOODEN_LADDERS, WOODEN_BEEHIVES, WOODEN_CHISELED_BOOKSHELVES, WOODEN_CHESTS, WOODEN_TRAPPED_CHESTS);
		this.tag(BlockTags.MINEABLE_WITH_HOE).addTag(LEAF_PILES);
		this.tag(BlockTags.BEEHIVES).addTag(WOODEN_BEEHIVES);
		this.tag(BlockTags.CLIMBABLE).addTag(WOODEN_LADDERS);
		this.tag(BlockTags.GUARDED_BY_PIGLINS).addTags(WOODEN_CHESTS, WOODEN_TRAPPED_CHESTS);
		this.tag(BlockTags.ENCHANTMENT_POWER_PROVIDER).addTag(WOODEN_BOOKSHELVES);

		this.tag(Tags.Blocks.CHESTS_WOODEN).addTags(WOODEN_CHESTS, WOODEN_TRAPPED_CHESTS);
		this.tag(Tags.Blocks.CHESTS_TRAPPED).addTag(WOODEN_TRAPPED_CHESTS);
		this.tag(Tags.Blocks.BOOKSHELVES).addTag(WOODEN_BOOKSHELVES);
		this.tag(LADDERS).addTag(WOODEN_LADDERS);
	}
}
