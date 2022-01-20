package com.teamabnormals.blueprint.core.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A class containing some simple methods for making tags.
 *
 * @author bageldotjpg
 */
public final class TagUtil {

	/**
	 * Creates a tag for a {@link Block}
	 *
	 * @param modid The namespace of the tag
	 * @param name  The name of the tag
	 * @return The created {@link Tag.Named<Block>}
	 */
	public static Tag.Named<Block> blockTag(String modid, String name) {
		return BlockTags.bind(modid + ":" + name);
	}

	public static Tag.Named<Block> forgeBlockTag(String name) {
		return blockTag("forge", name);
	}

	/**
	 * Creates a tag for an {@link Item}
	 *
	 * @param modid The namespace of the tag
	 * @param name  The name of the tag
	 * @return The created {@link Tag.Named<Item>}
	 */
	public static Tag.Named<Item> itemTag(String modid, String name) {
		return ItemTags.bind(modid + ":" + name);
	}

	public static Tag.Named<Item> forgeItemTag(String name) {
		return itemTag("forge", name);
	}

	/**
	 * Creates a tag for an {@link EntityType}
	 *
	 * @param modid The namespace of the tag
	 * @param name  The name of the tag
	 * @return The created {@link Tag.Named<EntityType>}
	 */
	public static Tag.Named<EntityType<?>> entityTypeTag(String modid, String name) {
		return EntityTypeTags.bind(modid + ":" + name);
	}

	public static Tag.Named<EntityType<?>> forgeEntityTypeTag(String name) {
		return entityTypeTag("forge", name);
	}

	/**
	 * Creates a tag for an {@link Enchantment}
	 *
	 * @param modid The namespace of the tag
	 * @param name  The name of the tag
	 * @return The created {@link Tag.Named<Enchantment>}
	 */
	public static Tag.Named<Enchantment> enchantmentTag(String modid, String name) {
		return ForgeTagHandler.makeWrapperTag(ForgeRegistries.ENCHANTMENTS, new ResourceLocation(modid, name));
	}

	public static Tag.Named<Enchantment> forgeEnchantmentTag(String name) {
		return enchantmentTag("forge", name);
	}

	/**
	 * Creates a tag for a {@link Potion}
	 *
	 * @param modid The namespace of the tag
	 * @param name  The name of the tag
	 * @return The created {@link Tag.Named<Potion>}
	 */
	public static Tag.Named<Potion> potionTag(String modid, String name) {
		return ForgeTagHandler.makeWrapperTag(ForgeRegistries.POTIONS, new ResourceLocation(modid, name));
	}

	public static Tag.Named<Potion> forgePotionTag(String name) {
		return potionTag("forge", name);
	}

	/**
	 * Creates a tag for a {@link BlockEntityType}
	 *
	 * @param modid The namespace of the tag
	 * @param name  The name of the tag
	 * @return The created {@link Tag.Named<BlockEntityType>}
	 */
	public static Tag.Named<BlockEntityType<?>> blockEntityTypeTag(String modid, String name) {
		return ForgeTagHandler.makeWrapperTag(ForgeRegistries.BLOCK_ENTITIES, new ResourceLocation(modid, name));
	}

	public static Tag.Named<BlockEntityType<?>> forgeBlockEntityTypeTag(String name) {
		return blockEntityTypeTag("forge", name);
	}

	/**
	 * Creates a tag for an {@link MobEffect}
	 *
	 * @param modid The namespace of the tag
	 * @param name  The name of the tag
	 * @return The created {@link Tag.Named<MobEffect>}
	 */
	public static Tag.Named<MobEffect> mobEffectTag(String modid, String name) {
		return ForgeTagHandler.makeWrapperTag(ForgeRegistries.MOB_EFFECTS, new ResourceLocation(modid, name));
	}

	public static Tag.Named<MobEffect> forgeMobEffectTag(String name) {
		return mobEffectTag("forge", name);
	}
}