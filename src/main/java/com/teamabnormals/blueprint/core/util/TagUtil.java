package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A class containing some simple methods for making tags.
 *
 * @author bageldotjpg
 */
public final class TagUtil {

	public static TagKey<Block> blockTag(String modid, String name) {
		return TagKey.create(Registries.BLOCK, new ResourceLocation(modid, name));
	}

	public static TagKey<Item> itemTag(String modid, String name) {
		return TagKey.create(Registries.ITEM, new ResourceLocation(modid, name));
	}

	public static TagKey<EntityType<?>> entityTypeTag(String modid, String name) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(modid, name));
	}

	public static TagKey<Enchantment> enchantmentTag(String modid, String name) {
		return TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(modid, name));
	}

	public static TagKey<Potion> potionTag(String modid, String name) {
		return TagKey.create(Registries.POTION, new ResourceLocation(modid, name));
	}

	public static TagKey<BlockEntityType<?>> blockEntityTypeTag(String modid, String name) {
		return TagKey.create(Registries.BLOCK_ENTITY_TYPE, new ResourceLocation(modid, name));
	}

	public static TagKey<MobEffect> mobEffectTag(String modid, String name) {
		return TagKey.create(Registries.MOB_EFFECT, new ResourceLocation(modid, name));
	}

	public static TagKey<Biome> biomeTag(String modid, String name) {
		return TagKey.create(Registries.BIOME, new ResourceLocation(modid, name));
	}

	public static boolean isTagged(Biome biome, TagKey<Biome> tagKey) {
		return ForgeRegistries.BIOMES.tags().getTag(tagKey).contains(biome);
	}

	public static TagKey<Level> dimensionTag(String modid, String name) {
		return TagKey.create(Registries.DIMENSION, new ResourceLocation(modid, name));
	}

	public static TagKey<DimensionType> dimensionTypeTag(String modid, String name) {
		return TagKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(modid, name));
	}

	public static TagKey<ConfiguredFeature<?, ?>> configuredFeatureTag(String modid, String name) {
		return TagKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(modid, name));
	}

	public static TagKey<PlacedFeature> placedFeatureTag(String modid, String name) {
		return TagKey.create(Registries.PLACED_FEATURE, new ResourceLocation(modid, name));
	}

	public static TagKey<Structure> structureTag(String modid, String name) {
		return TagKey.create(Registries.STRUCTURE, new ResourceLocation(modid, name));
	}

	public static TagKey<ConfiguredWorldCarver<?>> configuredCarverTag(String modid, String name) {
		return TagKey.create(Registries.CONFIGURED_CARVER, new ResourceLocation(modid, name));
	}
}