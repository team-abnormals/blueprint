package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlueprintBlockTagsProvider extends BlockTagsProvider {
	public BlueprintBlockTagsProvider(String modid, DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, modid, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(BlueprintBlockTags.HEDGES);
		this.tag(BlueprintBlockTags.LADDERS);
		this.tag(BlueprintBlockTags.VERTICAL_SLABS);
		this.tag(BlueprintBlockTags.WOODEN_VERTICAL_SLABS);
	}
}
