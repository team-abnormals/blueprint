package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.ForgeTagHandler;
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

	public static TagKey<NoiseGeneratorSettings> noiseSettingsTag(String modid, String name) {
		return TagKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(modid, name));
	}
}