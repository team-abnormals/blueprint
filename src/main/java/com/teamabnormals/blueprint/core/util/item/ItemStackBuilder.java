package com.teamabnormals.blueprint.core.util.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

/**
 * A simple utility class for building {@link ItemStack}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class ItemStackBuilder {
	private final ItemStack stack;

	public ItemStackBuilder(ItemStack stack) {
		this.stack = stack;
	}

	public ItemStackBuilder(ItemLike item) {
		this(new ItemStack(item));
	}

	/**
	 * Gets the stack being built.
	 *
	 * @return The {@link ItemStack} instance being built.
	 */
	public ItemStack getStack() {
		return this.stack;
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
	public ItemStackBuilder setUnbreakable(boolean showTooltip) {
		this.stack.set(DataComponents.UNBREAKABLE, new Unbreakable(showTooltip));
		return this;
	}

	/**
	 * Adds an enchantment with a level to the stack.
	 *
	 * @param enchantment The {@link Enchantment} to add.
	 * @param level       The level of the {@link Enchantment} to add.
	 * @return This builder.
	 */
	public ItemStackBuilder addEnchantment(Holder<Enchantment> enchantment, int level) {
		this.stack.enchant(enchantment, level);
		return this;
	}

	/**
	 * Sets the name of the stack.
	 *
	 * @param text The name to set.
	 * @return This builder.
	 */
	public ItemStackBuilder setName(@Nullable Component text) {
		this.stack.set(DataComponents.ITEM_NAME, text);
		return this;
	}

	/**
	 * Adds lore to the stack.
	 *
	 * @param text The lore text to add.
	 * @return This builder.
	 */
	public ItemStackBuilder addLore(Component text) {
		this.stack.update(DataComponents.LORE, ItemLore.EMPTY, lore -> lore.withLineAdded(text));
		return this;
	}

	/**
	 * Adds an {@link AttributeModifier} for an {@link Attribute} for an {@link EquipmentSlotGroup} on the stack.
	 *
	 * @param attribute The attribute to apply the {@link AttributeModifier} for.
	 * @param modifier  The {@link AttributeModifier} to apply to the {@link Attribute}.
	 * @param slotGroup The slot group for when the {@link AttributeModifier} should be applied.
	 * @return This builder.
	 */
	public ItemStackBuilder addAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slotGroup) {
		this.stack.update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, modifiers -> modifiers.withModifierAdded(attribute, modifier, slotGroup));
		return this;
	}

	/**
	 * @return The built stack.
	 */
	public ItemStack build() {
		return this.stack.copy();
	}
}
