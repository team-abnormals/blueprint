package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlueprintEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public BlueprintEntityTypeTagsProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
		super(output, lookupProvider, modid, fileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(BlueprintEntityTypeTags.FISHES).add(EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH);
		this.tag(BlueprintEntityTypeTags.MILKABLE).add(EntityType.COW, EntityType.MOOSHROOM, EntityType.GOAT);
	}

}