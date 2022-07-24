package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BlueprintBiomeTags {
	public static final TagKey<Biome> IS_GRASSLAND = TagUtil.biomeTag("forge", "is_grassland");
	public static final TagKey<Biome> IS_ICY = TagUtil.biomeTag("forge", "is_icy");
	public static final TagKey<Biome> IS_DESERT = TagUtil.biomeTag("forge", "is_desert");
	public static final TagKey<Biome> IS_OUTER_END = TagUtil.biomeTag("forge", "is_outer_end");

	public static final TagKey<Biome> WITH_DEFAULT_MONSTER_SPAWNS = TagUtil.biomeTag("forge", "with_default_monster_spawns");
	public static final TagKey<Biome> WITHOUT_DEFAULT_MONSTER_SPAWNS = TagUtil.biomeTag("forge", "without_default_monster_spawns");
}