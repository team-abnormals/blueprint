package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A class containing some simple methods for making tags.
 *
 * @author bageldotjpg
 */
public final class TagUtil {

	public static TagKey<Block> blockTag(String modid, String name) {
		return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<Item> itemTag(String modid, String name) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<EntityType<?>> entityTypeTag(String modid, String name) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<Enchantment> enchantmentTag(String modid, String name) {
		return TagKey.create(Registry.ENCHANTMENT_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<Potion> potionTag(String modid, String name) {
		return TagKey.create(Registry.POTION_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<BlockEntityType<?>> blockEntityTypeTag(String modid, String name) {
		return TagKey.create(Registry.BLOCK_ENTITY_TYPE_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<MobEffect> mobEffectTag(String modid, String name) {
		return TagKey.create(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<Biome> biomeTag(String modid, String name) {
		return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(modid, name));
	}

	public static boolean isTagged(Biome biome, TagKey<Biome> tagKey) {
		return ForgeRegistries.BIOMES.tags().getTag(tagKey).contains(biome);
	}

	public static TagKey<Level> dimensionTag(String modid, String name) {
		return TagKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<DimensionType> dimensionTypeTag(String modid, String name) {
		return TagKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<ConfiguredFeature<?, ?>> configuredFeatureTag(String modid, String name) {
		return TagKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<PlacedFeature> placedFeatureTag(String modid, String name) {
		return TagKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<ConfiguredStructureFeature<?, ?>> configuredStructureTag(String modid, String name) {
		return TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(modid, name));
	}

	public static TagKey<ConfiguredWorldCarver<?>> configuredCarverTag(String modid, String name) {
		return TagKey.create(Registry.CONFIGURED_CARVER_REGISTRY, new ResourceLocation(modid, name));
	}
}