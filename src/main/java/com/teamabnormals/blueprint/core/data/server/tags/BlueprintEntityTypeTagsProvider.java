package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintEntityTypeTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlueprintEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public BlueprintEntityTypeTagsProvider(String modid, DataGenerator generator, ExistingFileHelper fileHelper) {
		super(generator, modid, fileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(BlueprintEntityTypeTags.FISHES).add(EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH);
		this.tag(BlueprintEntityTypeTags.MILKABLE).add(EntityType.COW, EntityType.MOOSHROOM, EntityType.GOAT);
	}
}