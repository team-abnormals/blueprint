package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlueprintItemTagsProvider extends ItemTagsProvider {
	public BlueprintItemTagsProvider(String modid, DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper existingFileHelper) {
		super(generator, blockTags, modid, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(BlueprintItemTags.BUCKETS_EMPTY).add(Items.BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_WATER).add(Items.WATER_BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_LAVA).add(Items.LAVA_BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_MILK).add(Items.MILK_BUCKET);
		this.tag(BlueprintItemTags.BUCKETS_POWDER_SNOW).add(Items.POWDER_SNOW_BUCKET);

		this.tag(BlueprintItemTags.BOATABLE_CHESTS);
		this.copy(BlueprintBlockTags.LADDERS, BlueprintItemTags.LADDERS);
		this.copy(BlueprintBlockTags.VERTICAL_SLABS, BlueprintItemTags.VERTICAL_SLABS);
		this.copy(BlueprintBlockTags.WOODEN_VERTICAL_SLABS, BlueprintItemTags.WOODEN_VERTICAL_SLABS);
	}
}
