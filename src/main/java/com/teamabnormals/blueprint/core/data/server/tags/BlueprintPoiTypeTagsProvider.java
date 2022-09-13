package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintPoiTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlueprintPoiTypeTagsProvider extends PoiTypeTagsProvider {

	public BlueprintPoiTypeTagsProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Blueprint.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(PoiTypeTags.BEE_HOME).add(BlueprintPoiTypes.BEEHIVE.get());
	}

}
