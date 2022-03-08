package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class BlueprintEntityTypeTags {
	public static final TagKey<EntityType<?>> FISHES = TagUtil.entityTypeTag("forge", "fishes");
	public static final TagKey<EntityType<?>> MILKABLE = TagUtil.entityTypeTag("forge", "milkable");
}
