package com.minecraftabnormals.abnormals_core.core.util.item;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * A simple utility class for building {@link ItemStack}s.
 * @author SmellyModder (Luke Tonon)
 */
public class ItemStackBuilder {
	private final ItemStack stack;
	private final CompoundNBT tag;

	public ItemStackBuilder(ItemStack stack) {
		this.stack = stack;
		this.tag = stack.getOrCreateTag();
	}

	public ItemStackBuilder(IItemProvider item) {
		this(new ItemStack(item));
	}

	/**
	 * Sets the stack's count.
	 *
	 * @return This builder.
	 * @see ItemStack#setCount(int).
	 */
	public ItemStackBuilder setCount(int count) {
		this.stack.setCount(count);
		return this;
	}

	/**
	 * Grows the stack by an amount.
	 *
	 * @param amount Amount to grow the stack by.
	 * @return This builder.
	 * @see ItemStack#grow(int).
	 */
	public ItemStackBuilder grow(int amount) {
		this.stack.grow(amount);
		return this;
	}

	/**
	 * Shrinks the stack by an amount.
	 *
	 * @param amount Amount to shrink the stack by.
	 * @return This builder.
	 * @see ItemStack#shrink(int).
	 */
	public ItemStackBuilder shrink(int amount) {
		this.stack.shrink(amount);
		return this;
	}

	/**
	 * Sets the stack unbreakable.
	 *
	 * @return This builder.
	 */
	public ItemStackBuilder setUnbreakable() {
		this.tag.putBoolean("Unbreakable", true);
		return this;
	}

	/**
	 * Adds an enchantment with a level to the stack.
	 *
	 * @param enchantment The {@link Enchantment} to add.
	 * @param level       The level of the {@link Enchantment} to add.
	 * @return This builder.
	 */
	public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
		this.stack.enchant(enchantment, level);
		return this;
	}

	/**
	 * Sets the name of the stack.
	 *
	 * @param text The name to set.
	 * @return This builder.
	 * @see ItemStack#setDisplayName(ITextComponent).
	 */
	public ItemStackBuilder setName(@Nullable ITextComponent text) {
		this.stack.setHoverName(text);
		return this;
	}

	/**
	 * Adds lore to the stack.
	 *
	 * @param text The lore text to add.
	 * @return This builder.
	 */
	public ItemStackBuilder addLore(ITextComponent text) {
		CompoundNBT display = this.stack.getOrCreateTagElement("display");
		ListNBT loreListTag;
		if (display.contains("Lore", 9)) {
			loreListTag = display.getList("Lore", 8);
		} else {
			loreListTag = new ListNBT();
			display.put("Lore", loreListTag);
		}
		loreListTag.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(text)));
		return this;
	}

	/**
	 * Adds an {@link AttributeModifier} for an {@link Attribute} for an {@link EquipmentSlotType} on the stack.
	 *
	 * @param attribute The attribute to apply the {@link AttributeModifier} for.
	 * @param modifier  The {@link AttributeModifier} to apply to the {@link Attribute}.
	 * @param slot      The slot for when the {@link AttributeModifier} should be applied.
	 * @return This builder.
	 * @see ItemStack#addAttributeModifier(Attribute, AttributeModifier, EquipmentSlotType).
	 */
	public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier, @Nullable EquipmentSlotType slot) {
		this.stack.addAttributeModifier(attribute, modifier, slot);
		return this;
	}

	/**
	 * Adds an {@link AttributeModifier} for an {@link Attribute} for a multiple {@link EquipmentSlotType}s on the stack.
	 *
	 * @param attribute The attribute to apply the {@link AttributeModifier} for.
	 * @param modifier  The {@link AttributeModifier} to apply to the {@link Attribute}.
	 * @param slots     The slots for when the {@link AttributeModifier} should be applied.
	 * @return This builder.
	 * @see ItemStack#addAttributeModifier(Attribute, AttributeModifier, EquipmentSlotType).
	 * @see #addAttributeModifier(Attribute, AttributeModifier, EquipmentSlotType).
	 */
	public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier, EquipmentSlotType... slots) {
		for (EquipmentSlotType slot : slots) {
			this.stack.addAttributeModifier(attribute, modifier, slot);
		}
		return this;
	}

	/**
	 * Adds a predicate string tag for a predicate key.
	 * The two types of predicate keys are "CanDestroy" and "CanPlace".
	 *
	 * @param key       The predicate key.
	 * @param predicate The predicate string, this should be a string id.
	 * @return This builder.
	 */
	public ItemStackBuilder addPredicate(String key, String predicate) {
		ListNBT predicateList;
		if (this.tag.contains(key, 9)) {
			predicateList = this.tag.getList(key, 8);
		} else {
			predicateList = new ListNBT();
			this.tag.put(key, predicateList);
		}
		predicateList.add(StringNBT.valueOf(predicate));
		return this;
	}

	/**
	 * Adds a can destroy predicate for a specific block.
	 *
	 * @param block The block to mark as able to be destroyed.
	 * @return This builder.
	 */
	public ItemStackBuilder addCanDestroy(Block block) {
		return this.addPredicate("CanDestroy", ForgeRegistries.BLOCKS.getKey(block).toString());
	}

	/**
	 * Adds a can place on predicate for a specific block.
	 *
	 * @param block The block to mark as able to be placed on.
	 * @return This builder.
	 */
	public ItemStackBuilder addCanPlaceOn(Block block) {
		return this.addPredicate("CanPlaceOn", ForgeRegistries.BLOCKS.getKey(block).toString());
	}

	/**
	 * @return The built stack.
	 */
	public ItemStack build() {
		return this.stack.copy();
	}
}
