package com.teamabnormals.blueprint.core.data;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlueprintBlockTagsProvider extends BlockTagsProvider {
	public BlueprintBlockTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Blueprint.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(BlueprintBlockTags.HEDGES);
	}
}
