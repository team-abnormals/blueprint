package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

public class BlueprintBiomeTags {
	public static final TagKey<Biome> IS_OVERWORLD = TagUtil.biomeTag("forge", "is_overworld");
	public static final TagKey<Biome> IS_UNDERGROUND = TagUtil.biomeTag("forge", "is_underground");
	public static final TagKey<Biome> IS_END = TagUtil.biomeTag("forge", "is_end");
	public static final TagKey<Biome> IS_OUTER_END = TagUtil.biomeTag("forge", "is_outer_end");
}
