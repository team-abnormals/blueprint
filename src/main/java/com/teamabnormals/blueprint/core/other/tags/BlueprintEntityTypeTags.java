package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;

public class BlueprintEntityTypeTags {
	public static final Tag.Named<EntityType<?>> MILKABLE = TagUtil.forgeEntityTypeTag("milkable");
}
