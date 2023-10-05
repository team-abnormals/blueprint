package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlueprintItemTagsProvider extends ItemTagsProvider {

	public BlueprintItemTagsProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> tagLookup, ExistingFileHelper fileHelper) {
		super(output, lookupProvider, tagLookup, modid, fileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider p_256380_) {
		this.tag(BlueprintItemTags.CHICKEN_FOOD);
		this.tag(BlueprintItemTags.PIG_FOOD);
		this.tag(BlueprintItemTags.STRIDER_FOOD);
		this.tag(BlueprintItemTags.STRIDER_TEMPT_ITEMS);
		this.tag(BlueprintItemTags.OCELOT_FOOD);
		this.tag(BlueprintItemTags.CAT_FOOD);

		this.tag(BlueprintItemTags.MILK).addTag(BlueprintItemTags.BUCKETS_MILK);
		this.tag(BlueprintItemTags.PUMPKINS).add(Items.PUMPKIN);

		this.tag(BlueprintItemTags.BUCKETS_EMPTY).add(Items.BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_WATER).add(Items.WATER_BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_LAVA).add(Items.LAVA_BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_MILK).add(Items.MILK_BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_POWDER_SNOW).add(Items.POWDER_SNOW_BUCKET);
		this.tag(BlueprintItemTags.BUCKETS).addTag(BlueprintItemTags.BUCKETS_EMPTY).addTag(BlueprintItemTags.BUCKETS_WATER).addTag(BlueprintItemTags.BUCKETS_LAVA).addTag(BlueprintItemTags.BUCKETS_MILK).addTag(BlueprintItemTags.BUCKETS_LAVA);

		this.tag(BlueprintItemTags.TOOLS_AXES).add(Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
		this.tag(BlueprintItemTags.TOOLS_HOES).add(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
		this.tag(BlueprintItemTags.TOOLS_PICKAXES).add(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
		this.tag(BlueprintItemTags.TOOLS_SHOVELS).add(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
		this.tag(BlueprintItemTags.TOOLS_SWORDS).add(Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
		this.tag(BlueprintItemTags.TOOLS).addTag(BlueprintItemTags.TOOLS_AXES).addTag(BlueprintItemTags.TOOLS_HOES).addTag(BlueprintItemTags.TOOLS_PICKAXES).addTag(BlueprintItemTags.TOOLS_SHOVELS).addTag(BlueprintItemTags.TOOLS_SWORDS);

		this.tag(BlueprintItemTags.FURNACE_BOATS);
		this.tag(BlueprintItemTags.LARGE_BOATS);
	}
}
