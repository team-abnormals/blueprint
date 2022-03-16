package com.teamabnormals.blueprint.core.other.tags;

import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

public class BlueprintBiomeTags {
	public static final TagKey<Biome> IS_OVERWORLD = TagUtil.biomeTag("forge", "is_overworld");

	public static final TagKey<Biome> IS_EXTREME_HILLS = TagUtil.biomeTag("forge", "is_extreme_hills");
	public static final TagKey<Biome> IS_PLAINS = TagUtil.biomeTag("forge", "is_plains");
	public static final TagKey<Biome> IS_ICY = TagUtil.biomeTag("forge", "is_icy");
	public static final TagKey<Biome> IS_SAVANNA = TagUtil.biomeTag("forge", "is_savanna");
	public static final TagKey<Biome> IS_DESERT = TagUtil.biomeTag("forge", "is_desert");
	public static final TagKey<Biome> IS_SWAMP = TagUtil.biomeTag("forge", "is_swamp");
	public static final TagKey<Biome> IS_MUSHROOM = TagUtil.biomeTag("forge", "is_mushroom");
	public static final TagKey<Biome> IS_UNDERGROUND = TagUtil.biomeTag("forge", "is_underground");

	public static final TagKey<Biome> IS_END = TagUtil.biomeTag("forge", "is_end");
	public static final TagKey<Biome> IS_OUTER_END = TagUtil.biomeTag("forge", "is_outer_end");

	public static final TagKey<Biome> WITHOUT_MONSTER_SPAWNS = TagUtil.biomeTag("forge", "without_monster_spawns");
}